from nltk import SnowballStemmer
from sklearn.feature_extraction.text import TfidfVectorizer  # skleran для классификации текста
from sklearn.linear_model import SGDClassifier
from sklearn.pipeline import Pipeline
# from Stemmer import Stemmer  # Stemmer нужен для приведения слов к начальной форме
import numpy as np  # numpy для работы с массивами
import sys
import re  # регулярные выражения для очистки текста


def cleaner(text):
    text = text.lower()
    stemmer = SnowballStemmer("russian")  # создаем экземпляр класса Stemmer для русского языка
    text = " ".join(stemmer.stem(text.split()))  # применяем стемминг к тексту (приводим слова к начальной форме)
    text = re.sub(r'\b\d+\b', ' digit ', text)  # заменяем числа на слово digit
    return text


def load(list_chats):
    data = {'intent': [], 'response': []}  # словарь для хранения данных

    for key in list_chats:
        for row in list_chats[key]:
            data['intent'] += [row]
            data['response'] += [key]

    return data


def train_test_split(data,
                     validation_split=0.2):  # функция разбиения выборки на обучающую и тестовую. validation_split - доля тестовой выборки
    size = len(data['intent'])  # размер выборки
    indices = np.arange(size)  # создаем массив индексов
    np.random.shuffle(indices)  # перемешиваем массив индексов

    x = [data['intent'][i] for i in indices]  # создание массива из текстов
    y = [data['response'][i] for i in indices]  # создание массива из ответов
    validation_samples = int(validation_split * size)  # определяем размер валидационной выборки

    return {
        'train': {'x': x[:-validation_samples], 'y': y[:-validation_samples]},  # обучающая выборка
        'test': {'x': x[-validation_samples:], 'y': y[-validation_samples:]}  # тестовая выборка
    }


def model(list_chats, list_chats_without_folder):
    data = load(list_chats)  # загружаем данные
    sample = train_test_split(data)  # разбиваем выборку на обучающую и тестовую
    pipeline = Pipeline([
        ('tfidf', TfidfVectorizer()),  # tfidf векторизация текста (преобразование текста в вектор)
        ('clf', SGDClassifier(loss='hinge'))
        # классификатор (метод опорных векторов) с функцией потерь hinge нужен для классификации текста
    ])  # создаем модель

    pipeline.fit(sample['train']['x'], sample['train']['y'])  # обучаем модель
    predicted = pipeline.predict(sample['train']['x'])  # предсказываем результаты обучающей выборки
    # print(np.mean(predicted == sample['train']['y']))  # считаем точность обучающей выборки

    data_chats = {'intent': [], 'response': []}

    for key in list_chats_without_folder:
        for row in list_chats_without_folder[key]:
            data_chats['intent'] += [row]
            data_chats['response'] += [key]

    # print(data_chats)
    result_data = {'intent': [], 'result': []}

    for i in range(len(data_chats['intent'])):
        intent = data_chats['intent'][i]
        intents = [intent]
        predicted = pipeline.predict(intents)  # предсказываем результаты тестовой выборки
        # print(predicted[0])  # выводим результат
        result_data['intent'] += [data_chats['response'][i]]
        result_data['result'] += [predicted[0]]

    # print(result_data)
    result_x = {'intent': []}
    for key in list_chats_without_folder:
        result_x['intent'] += [key]

    result = {}

    for key in result_x['intent']:
        res = np.zeros(len(list_chats)).astype(np.int64)
        for i in range(len(result_data['intent'])):
            for j in range(len(res)):
                if result_data['intent'][i] == key:
                    if result_data['result'][i] == j+2:
                        res[j] += 1
        max = 0
        for i in range(len(res)):
            if res[i] > max:
                max = res[i]
                q = i + 2
        result[key] = q
    return result
