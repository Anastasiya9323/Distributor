from telethon import TelegramClient
from telethon.tl import functions
from telethon.tl.functions.messages import UpdateDialogFilterRequest
from telethon.tl.types import ChannelParticipantsAdmins, ChannelParticipantsSearch, InputPeerUser, InputPeerChannel, \
    User

import config

import os

api_id = config.api_id
api_hash = config.api_hash
session = os.environ["HOME"] + "/" + "session.session"


async def create_session(phone):
    if os.path.exists(session):
        os.remove(session)
    client = TelegramClient(session, api_id, api_hash, system_version="4.16.30-vxCUSTOM")
    await client.connect()

    # This will send the code to the user. You have to get it using the front end
    code = await client.send_code_request(phone)

    phone_code_hash = code.phone_code_hash

    return phone_code_hash


async def create_session2(phone, code, phone_code_hash):
    client = TelegramClient(session, api_id, api_hash, system_version="4.16.30-vxCUSTOM")
    await client.connect()
    await client.sign_in(phone, code=code, phone_code_hash=phone_code_hash)
    await client.disconnect()


async def grab_folders(chatFlag, channelFlag):
    list_dialogs_folders = dict()
    list_dialog = []

    async with TelegramClient(session, api_id, api_hash, system_version="4.16.30-vxCUSTOM") as client:
        # Получаем список всех диалогов
        dialogs = await client.get_dialogs()

        for dialog in dialogs:
            if type(dialog.input_entity) == InputPeerUser:
                list_dialog.append(dialog.input_entity.user_id)
            if type(dialog.input_entity) == InputPeerChannel:
                list_dialog.append(dialog.input_entity.channel_id)

        # Получаем список всех папок
        request = await client(functions.messages.GetDialogFiltersRequest())
        for dialog_filter in request.filters:
            if dialog_filter != request.filters[0]:
                list_dialogs_folders[dialog_filter.id] = []
                for peer in dialog_filter.include_peers:
                    if type(peer) == InputPeerUser:
                        list_dialogs_folders[dialog_filter.id].append(peer.user_id)
                        if peer.user_id in list_dialog:
                            list_dialog.remove(peer.user_id)
                        if not chatFlag:
                            list_dialog.remove(peer.user_id)
                    if type(peer) == InputPeerChannel and channelFlag:
                        list_dialogs_folders[dialog_filter.id].append(peer.channel_id)
                        if peer.channel_id in list_dialog:
                            list_dialog.remove(peer.channel_id)
                        if not channelFlag:
                            list_dialog.remove(peer.channel_id)

        for dialog in dialogs:
            if type(dialog.input_entity) == InputPeerUser and dialog.input_entity.user_id in list_dialog and not chatFlag:
                list_dialog.remove(dialog.input_entity.user_id)
            if type(dialog.input_entity) == InputPeerChannel and dialog.input_entity.channel_id in list_dialog and not channelFlag:
                list_dialog.remove(dialog.input_entity.channel_id)


    return list_dialogs_folders, list_dialog


async def grab_folders_count():
    list_dialogs_folders = dict()
    list_dialog = []

    async with TelegramClient(session, api_id, api_hash, system_version="4.16.30-vxCUSTOM") as client:
        # Получаем список всех диалогов
        dialogs = await client.get_dialogs()
        count_dialogs = len(dialogs)

        for dialog in dialogs:
            if type(dialog.input_entity) == InputPeerUser:
                list_dialog.append(dialog.input_entity.user_id)
            if type(dialog.input_entity) == InputPeerChannel:
                list_dialog.append(dialog.input_entity.channel_id)

        # Получаем список всех папок
        request = await client(functions.messages.GetDialogFiltersRequest())
        folder_dialogs_count = 0
        for dialog_filter in request.filters:
            if dialog_filter != request.filters[0]:

                list_dialogs_folders[dialog_filter.id] = []
                for peer in dialog_filter.include_peers:
                    folder_dialogs_count += 1
                    if type(peer) == InputPeerUser:
                        list_dialogs_folders[dialog_filter.id].append(peer.user_id)
                        if peer.user_id in list_dialog:
                            list_dialog.remove(peer.user_id)
                    if type(peer) == InputPeerChannel:
                        list_dialogs_folders[dialog_filter.id].append(peer.channel_id)
                        if peer.channel_id in list_dialog:
                            list_dialog.remove(peer.channel_id)

    return [count_dialogs, len(list_dialogs_folders), folder_dialogs_count, len(list_dialog)]


async def dump_messages(chat, list_messages):
    """Выгружаем сообщения"""
    async with TelegramClient(session, api_id, api_hash, system_version="4.16.30-vxCUSTOM") as client:
        n = 0
        async for message in client.iter_messages(chat):
            n += 1
            if message.message != "" and message.message is not None:
                list_messages.append(message.message)
            if n == 100:
                break
    return list_messages


async def dump_folder_messages(chat, list_messages):
    """Выгружаем сообщения"""
    async with TelegramClient(session, api_id, api_hash, system_version="4.16.30-vxCUSTOM") as client:
        n = 0
        async for message in client.iter_messages(chat):
            n += 1
            if message.message != "" and message.message is not None:
                list_messages.append(message.message)
            if n == 100:
                break
    return list_messages


async def put_to_folder(chat, folder):
    async with TelegramClient(session, api_id, api_hash, system_version="4.16.30-vxCUSTOM") as client:
        # Получаем список всех папок
        request = await client(functions.messages.GetDialogFiltersRequest())
        # Добавление в папку (юзер и ченел отличаются)
        entity = await client.get_entity(chat)
        if type(entity) == User: #and entity.id != 777000:
            to_add = InputPeerUser(entity.id, entity.access_hash)  # i get user entity and convert it to PeerUser
        else:
            to_add = InputPeerChannel(entity.id, entity.access_hash)
        for i in range(len(request.filters)):
            if request.filters[i] != request.filters[0]:
                if request.filters[i].id == folder:
                    request.filters[i].include_peers.append(to_add)  # i add to_add to the list of other users in DialogFilter
                    request = await client(UpdateDialogFilterRequest(folder, request.filters[i]))
                    break
