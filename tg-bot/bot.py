import asyncio
import logging
import requests
import json

from aiogram import Bot, Dispatcher, types
from aiogram.filters.command import Command
from aiogram.fsm.context import FSMContext
from aiogram.fsm.state import StatesGroup, State
from enum import Enum

BASE_URL = "http://localhost:8080/v0/api"
BOT_STATE = "bot_state"

CREATE_MEET_USERS = "users"
CREATE_MEET_TIME_START = "time_start"
CREATE_MEET_TIME_END = "time_end"
CREATE_MEET_DURATION = "duration"
CREATE_BUSY_START_TIME = "start_time"
CREATE_BUSY_END_TIME = "end_time"

# Включаем логирование, чтобы не пропустить важные сообщения
logging.basicConfig(level=logging.INFO)
# Объект бота
bot = Bot(token="6577165668:AAFW1X9mgW2KkyxglBJj7Q7uF7hpfqzm2q0")
# Диспетчер
dp = Dispatcher()
context_dict = dict()


class BotStates(Enum):
    AWAITING_LOGIN = 0
    AWAITING_TIME_START = 1
    AWAITING_TIME_END = 2
    AWAITING_DURATION = 3
    AWAITING_BUSY_START_TIME = 4
    AWAITING_BUSY_END_TIME = 5


class UserState(StatesGroup):
    bot_state = State()


# Хэндлер на команду /start
@dp.message(Command("start"))
async def cmd_start(message: types.Message):
    register_body = {
        "username": message.from_user.username,
        "user_id": message.from_user.id
    }

    response = requests.post(f"{BASE_URL}/bot-start", json=register_body)
    logging.log(logging.INFO, f"")
    await message.answer("Привет! Я бот по созданию встреч, я помогу быстро согласовать время встречи для группы людей."
                         "Я умею хранить информацию о встречах пользователей и на их основе могу найти доступный"
                         "диапазон времени, в который всем удобно созвониться.")


@dp.message(Command("create_meet"))
async def cmd_create_meet(message: types.Message, state: FSMContext):
    await state.set_state(UserState.bot_state)
    await state.update_data(bot_state=BotStates.AWAITING_LOGIN)
    await message.answer("Введите логины пользователей телеграм через запятую:")


@dp.message(Command("my_meets"))
async def cmd_get_my_meets(message: types.Message):
    response = requests.get(f"{BASE_URL}/meet", params={'username': message.from_user.username})
    response_dict = json.loads(response.text)
    result = ""
    for i in range(len(response_dict['meets'])):
        meet = response_dict['meets'][i]
        result += f"Встреча {i + 1}: начало в {meet['time_start']}; продолжительность: {meet['duration']} минут; " \
                  f"приглашенные пользователи: {meet['users_nicks']}\n"
    await message.answer(result)


@dp.message(Command("create_busy"))
async def cmd_create_busy(message: types.Message, state: FSMContext):
    await state.set_state(UserState.bot_state)
    await state.update_data(bot_state=BotStates.AWAITING_BUSY_START_TIME)
    await message.answer("Введите дату начала занятости (в формате YYYY-MM-DD HH-MM)")


@dp.message()
async def query_message(message: types.Message, state: FSMContext):
    data = await state.get_data()
    if BOT_STATE not in data:
        await message.answer("Не понимаю, что вы от меня хотите")
    elif data[BOT_STATE] == BotStates.AWAITING_LOGIN:
        await accept_login(message)
        await state.update_data(bot_state=BotStates.AWAITING_TIME_START)
        await message.answer(
            "Введите дату начала диапазона поиска свободного временнего диапазона (в формате YYYY-MM-DD)"
        )
    elif data[BOT_STATE] == BotStates.AWAITING_TIME_START:
        await accept_time_start(message, state)
        await state.update_data(bot_state=BotStates.AWAITING_TIME_END)
        await message.answer(
            "Введите дату конца диапазона поиска свободного временнего диапазона (в формате YYYY-MM-DD)"
        )
    elif data[BOT_STATE] == BotStates.AWAITING_TIME_END:
        await accept_time_end(message, state)
        await state.update_data(bot_state=BotStates.AWAITING_DURATION)
        await message.answer( "Введите длительность встречи (в минутах, например: 30)")
    elif data[BOT_STATE] == BotStates.AWAITING_DURATION:
        await accept_duration(message, state)
        await state.clear()

        user_id = message.from_user.id

        body = {
            CREATE_MEET_USERS: context_dict[user_id][CREATE_MEET_USERS],
            CREATE_MEET_TIME_START: f"{context_dict[user_id][CREATE_MEET_TIME_START]} 00:00",
            CREATE_MEET_TIME_END: f"{context_dict[user_id][CREATE_MEET_TIME_END]} 23:59",
            CREATE_MEET_DURATION: int(context_dict[user_id][CREATE_MEET_DURATION])
        }

        response = requests.post(f"{BASE_URL}/meet", json=body)
        if response.ok is True:
            response_dict = json.loads(response.text)

            for user_id_to_send in response_dict['users_ids']:
                if int(user_id_to_send) != message.from_user.id:
                    await bot.send_message(chat_id=user_id_to_send, text=f"Вас пригласили на встречу \nВремя начала встречи: {response_dict['time_start']} \nПродолжительность встречи: {response_dict['duration']} минут \nПриглашенные пользователи: {response_dict['users_nicks']}")

            logins = ["@" + login for login in response_dict['users_nicks']]
            await message.answer("Встреча успешно создалась, все приглашенные пользователи уведомлены"
                                 f"\nВремя начала встречи: {response_dict['time_start']}"
                                 f"\nПродолжительность встречи: {response_dict['duration']} минут"
                                 f"\nПриглашенные пользователи: {logins}")
        else:
            await message.answer(f"Не удалось создать встречу")
    elif data[BOT_STATE] == BotStates.AWAITING_BUSY_START_TIME:
        await accept_busy_start_time(message)
        await state.update_data(bot_state=BotStates.AWAITING_BUSY_END_TIME)
        await message.answer("Введите дату конца занятости (в формате YYYY-MM-DD HH-MM)")
    elif data[BOT_STATE] == BotStates.AWAITING_BUSY_END_TIME:
        await accept_busy_end_time(message, state)
        await state.clear()

        username = message.from_user.username
        user_id = message.from_user.id
        body = {
            CREATE_BUSY_START_TIME: context_dict[user_id][CREATE_BUSY_START_TIME],
            CREATE_BUSY_END_TIME: context_dict[user_id][CREATE_BUSY_END_TIME]
        }
        response = requests.post(f"{BASE_URL}/user/{username}", json=body)
        if response.ok is True:
            await message.answer("Отсутсвие успешно создано")
        else:
            await message.answer("Что-то пошло не так. Попробуйте снова")


async def accept_login(message: types.Message):
    user_id = message.from_user.id
    users = message.text.split(',')
    if user_id not in context_dict:
        context_dict[user_id] = dict()
    context_dict[user_id][CREATE_MEET_USERS] = users


async def accept_time_start(message: types.Message, state: FSMContext):
    user_id = message.from_user.id
    if user_id not in context_dict:
        await message.answer("Что-то пошло не так. Начните сначала")
        await state.clear()
        return

    context_dict[user_id][CREATE_MEET_TIME_START] = message.text


async def accept_time_end(message: types.Message, state: FSMContext):
    user_id = message.from_user.id
    if user_id not in context_dict:
        await message.answer("Что-то пошло не так. Начните сначала")
        await state.clear()
        return

    context_dict[user_id][CREATE_MEET_TIME_END] = message.text


async def accept_duration(message: types.Message, state: FSMContext):
    user_id = message.from_user.id
    if user_id not in context_dict:
        await message.answer("Что-то пошло не так. Начните сначала")
        await state.clear()
        return

    context_dict[user_id][CREATE_MEET_DURATION] = message.text


async def accept_busy_start_time(message: types.Message):
    user_id = message.from_user.id
    if user_id not in context_dict:
        context_dict[user_id] = dict()
    context_dict[user_id][CREATE_BUSY_START_TIME] = message.text


async def accept_busy_end_time(message: types.Message,  state: FSMContext):
    user_id = message.from_user.id
    if user_id not in context_dict:
        await message.answer("Что-то пошло не так. Начните сначала")
        await state.clear()
        return

    context_dict[user_id][CREATE_BUSY_END_TIME] = message.text


# Запуск процесса поллинга новых апдейтов
async def main():
    await dp.start_polling(bot)


if __name__ == "__main__":
    asyncio.run(main())
