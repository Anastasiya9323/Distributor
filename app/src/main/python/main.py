import asyncio
import os

from machine_learning import model
from my_functions import *

api_id = config.api_id
api_hash = config.api_hash
session = os.environ["HOME"] + "/" + "session.session"
loop = asyncio.get_event_loop()


def exist_session():
    if os.path.exists(session):
        return True
    else:
        return False


def send_phone(phone):
    phone_code_hash = loop.run_until_complete(create_session(phone))
    return phone_code_hash


def get_access(phone, code, phone_code_hash ):
    loop.run_until_complete(create_session2(phone, code, phone_code_hash))


def get_dialogs():
    list = loop.run_until_complete(grab_folders_count())
    return list


def put_dialogs(chatFlag, channelFlag):
    list_dialogs_folder, list_dialog = loop.run_until_complete(grab_folders(chatFlag, channelFlag))

    list_messages_without_folder = dict()

    for dialog in list_dialog:
        list_messages_without_folder[dialog] = []
        list_messages_without_folder[dialog] = loop.run_until_complete(dump_messages(dialog, list_messages_without_folder[dialog]))

    list_messages_folders = dict()

    for folder in list_dialogs_folder:
        list_messages_folders[folder] = []
        for chat in list_dialogs_folder[folder]:
            list_messages_folders[folder] = loop.run_until_complete(dump_folder_messages(chat, list_messages_folders[folder]))

    result = model(list_messages_folders, list_messages_without_folder)

    count_dialog = 0

    for chat in result:
        loop.run_until_complete(put_to_folder(chat, result[chat]))
        count_dialog += 1

    return count_dialog
