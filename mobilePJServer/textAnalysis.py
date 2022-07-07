from konlpy.tag import Kkma
from konlpy.tag import Okt
#from nltk.tokenize import word_tokenize 
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.preprocessing import normalize
import numpy as np
import re

text_file = open("./stopwords2.txt", 'r', encoding='UTF8')

stopwords = [line.rstrip() for line in text_file]

test_file = open("./audio_only.txt", "r", encoding='UTF8')
dialog = [line.rstrip() for line in test_file]

while '' in dialog:
  dialog.remove('')

sentences = dialog

"""##get_nouns"""

#kkma = Kkma() 
okt = Okt()
words = []

for sentence in sentences:
  words.append(okt.nouns(sentence))

words = sum(words, [])

for stop in stopwords:
  if stop in words: 
    words.remove(stop)

print(words)



"""##GraphMatrix"""

tfidf = TfidfVectorizer()
cnt_vec = CountVectorizer()
sentence_graph = []

tfidf_mat = tfidf.fit_transform(words).toarray()
sentence_graph = np.dot(tfidf_mat, tfidf_mat.T)

print(sentence_graph)

data = cnt_vec.fit_transform(words).toarray()
cnt_vec_mat = normalize(data, axis=0)
vocab = cnt_vec.vocabulary_

words_graph = np.dot(cnt_vec_mat.T, cnt_vec_mat)
idx2word = {vocab[word] : word for word in vocab}

"""##Rank"""

A = sentence_graph
d = 0.85
matrix_size = A.shape[0]

for id in range(matrix_size):
      A[id, id] = 0 # diagonal 부분을 0으로
      link_sum = np.sum(A[:,id]) # A[:, id] = A[:][id]
      if link_sum != 0:
            A[:, id] /= link_sum
      A[:, id] *= -d
      A[id, id] = 1

B = (1-d) * np.ones((matrix_size, 1))
ranks = np.linalg.solve(A, B) # 연립방정식 Ax = b

sentence_graph_idx = {idx: r[0] for idx, r in enumerate(ranks)}

sorted_sentence_rank_idx = sorted(sentence_graph_idx, key=lambda k: sentence_graph_idx[k], reverse=True)

A = words_graph
d = 0.85
matrix_size = A.shape[0]

for id in range(matrix_size):
      A[id, id] = 0 # diagonal 부분을 0으로
      link_sum = np.sum(A[:,id]) # A[:, id] = A[:][id]
      if link_sum != 0:
            A[:, id] /= link_sum
      A[:, id] *= -d
      A[id, id] = 1

B = (1-d) * np.ones((matrix_size, 1))
ranks = np.linalg.solve(A, B) # 연립방정식 Ax = b

words_graph_idx = {idx: r[0] for idx, r in enumerate(ranks)}
print(words_graph_idx)

sorted_words_rank_idx = sorted(words_graph_idx, key=lambda k: words_graph_idx[k], reverse=True)
print(sorted_words_rank_idx)

"""###Summarize"""

sent_num=3
summary = []
index=[]

for idx in sorted_sentence_rank_idx[:sent_num]:
      index.append(idx)

index.sort()
for idx in index:
      summary.append(sentences[idx])

"""###Keywords"""

word_num=10
keywords = []
index=[]

for idx in sorted_words_rank_idx[:word_num]:
      index.append(idx)
    
#index.sort()
for idx in index:
      keywords.append(idx2word[idx])

"""##Result"""

count = 0

for row in summary:
    print(row)
    print()
    count = count + 1
    if(count > 3):
        break

print('keywords :',keywords)
