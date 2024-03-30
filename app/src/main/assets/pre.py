


import re



data = ""

with open('inwords.txt') as file_handle:
    data = file_handle.read()


matched = []


for word in data.split('\n'):
    word = word.strip()
    mat = re.findall('^[a-zA-Z]{4,6}$', word)
    if mat:
        matched.append(mat[0].lower())

f_matched = []

for ma in matched:
    if len(re.findall('([aeiou])', ma)) >= 2:
        f_matched.append(ma)

with open('words.txt', 'wb') as file_handle:
    for ma in f_matched:
        file_handle.write(ma.encode())
        file_handle.write('\n'.encode())

