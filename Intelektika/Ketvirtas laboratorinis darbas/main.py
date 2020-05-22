import csv
from pandas import DataFrame
import numpy as np
import matplotlib.pyplot as plot
import seaborn as sb
import math


def Heat(nameList, Data):
    fig, ax = plot.subplots(figsize=(15, 15))
    df = DataFrame(Data, columns=nameList)
    corrMatrix = df.corr()
    ax = sb.heatmap(corrMatrix, annot=True)
    plot.show()

class K_Means:

    def __init__(self, k=2, tol=0.001, max_iter=300):
        self.k = k
        self.tol = tol
        self.max_iter = max_iter

    def fit(self, data, centroids):

        self.centroids = {}

        for i in range(self.k):
            self.centroids[i] = centroids[i]

        for i in range(self.max_iter):
            self.classifications = {}
            self.labels_ = []
            for i in range(self.k):
                self.classifications[i] = []

            for featureset in data:
                distances = [np.linalg.norm(featureset - self.centroids[centroid]) for centroid in self.centroids]  # norm vektoriaus esme kaip normalus foras
                classification = distances.index(min(distances))  # gaunamas indeksas
                self.labels_.append(classification) # pridedamas klasterio indeksas
                self.classifications[classification].append(featureset)  # nustatoma kuris klusteris

            prev_centroids = dict(self.centroids)

            for classification in self.classifications:
                self.centroids[classification] = np.average(self.classifications[classification], axis=0)  # perskaiciuojami centroidai

            optimized = True

            for c in self.centroids: # erroru skaiciavimas ir patikrinamas ar testi skaiciavimus
                original_centroid = prev_centroids[c]
                current_centroid = self.centroids[c]
                if np.sum(original_centroid) > 0:
                    if (np.sum((current_centroid - original_centroid)) / np.sum(
                            original_centroid) * 100.0) > self.tol:
                        optimized = False
                else:
                    optimized = False


            if optimized:
                break

def sortBy(e): # saraso rikiavimo funkcija
    return math.pow((e[0] + e[1]),2)



dataList = [] # duomenu is failo nuskaitymas
with open('players_stats.csv') as csvfile:
  reader = csv.reader(csvfile)
  for row in reader:
        dataList.append(row)

dataUsingKey = {}
keys = [] # duomenu pavadinimai naudojami kaip raktai pasiekti duomenims
cnt = 0

# raktu panaudojimo paruosimas
for x in dataList[0]:
    if cnt > 0 and cnt < 22:
        dataUsingKey[x] = []
        keys.append(x)
    cnt = cnt + 1

index = 0
for x in dataList[1:]: # duomenu pridejimas i dictionary
    index = 0
    for a in x[1:22]:
        dataUsingKey[keys[index]].append(float(a))
        index = index + 1

Heat(keys, dataUsingKey) # heat map peisimas

range_n_clusters = [2, 3, 4, 5, 6] # klasteriu kiekiu sarasas
pairs = [[8,12], [6,17], [7,16]] # naudojamu prou indeksai

class K_Means:

    def __init__(self, k=2, tol=0.001, max_iter=300):
        self.k = k
        self.tol = tol
        self.max_iter = max_iter

    def fit(self, data, centroids):

        self.centroids = {}

        for i in range(self.k):
            self.centroids[i] = centroids[i]

        for i in range(self.max_iter):
            self.classifications = {}
            self.labels_ = []
            for i in range(self.k):
                self.classifications[i] = []

            for featureset in data:
                distances = [np.linalg.norm(featureset - self.centroids[centroid]) for centroid in self.centroids]  # skaičiuojamas atstumas tap taškų
                classification = distances.index(min(distances))  # gaunamas indeksas
                self.labels_.append(classification) # pridedamas klasterio indeksas
                self.classifications[classification].append(featureset)  # kordinačių įdėjimas į klasterį

            prev_centroids = dict(self.centroids)

            for classification in self.classifications:
                self.centroids[classification] = np.average(self.classifications[classification], axis=0)  # perskaiciuojami centroidai

            optimized = True

            for c in self.centroids: # erorų skaičiavimas ir patikrinamas ar tęsti skaičiavimus
                original_centroid = prev_centroids[c]
                current_centroid = self.centroids[c]
                if np.sum(original_centroid) > 0:
                    if (np.sum((current_centroid - original_centroid)) / np.sum(
                            original_centroid) * 100.0) > self.tol:
                        optimized = False
                else:
                    optimized = False


            if optimized:
                break

def sortBy(e): # sarašo rikiavimo funkcija
    return math.pow((e[0] + e[1]),2)



dataList = [] # duomenų is failo nuskaitymas
with open('players_stats.csv') as csvfile:
  reader = csv.reader(csvfile)
  for row in reader:
        dataList.append(row)

dataUsingKey = {}
keys = [] # duomenų pavadinimai naudojami kaip raktai pasiekti duomenims
cnt = 0

# raktų panaudojimo paruošimas
for x in dataList[0]:
    if cnt > 0 and cnt < 22:
        dataUsingKey[x] = []
        keys.append(x)
    cnt = cnt + 1

index = 0
for x in dataList[1:]: # duomenų pridėjimas į dictionary
    index = 0
    for a in x[1:22]:
        dataUsingKey[keys[index]].append(float(a))
        index = index + 1

range_n_clusters = [2, 3, 4, 5, 6] # klasterių kiekių sarašas
pairs = [[8,12], [6,17], [7,16]] # naudojamu proų indeksai

for i in range(len(pairs)):

    x_index = pairs[i][0]
    y_index = pairs[i][1]

    dataSorting = []
    coordinates = []
    data = []

    # sarašo paruošimas centroidų paieškai
    for a in range(len(dataUsingKey[keys[y_index]])):
        dataSorting.append([dataUsingKey[keys[x_index]][a], dataUsingKey[keys[y_index]][a]])
        data.append([dataUsingKey[keys[x_index]][a], dataUsingKey[keys[y_index]][a]])

    dataSorting.sort(key=sortBy)

    for k_count in range_n_clusters:

        # centroidu paieška atsižvelgiant į klasterių kiekį
        init_centroids = [[dataSorting[0][0], dataSorting[0][1]]]
        if k_count > 2:
            splitTo = int(len(dataSorting) / (k_count - 1))
            x = splitTo
            while x < splitTo * (k_count - 1):
                init_centroids.append([dataSorting[x][0], dataSorting[x][1]])
                x = x + splitTo

        init_centroids.append([dataSorting[len(dataSorting) - 1][0], dataSorting[len(dataSorting) - 1][1]])

        # sukurto modelio panaudojimas
        kmeans = K_Means(k_count)
        kmeans.fit(np.array(data), np.array(init_centroids))
        lables = kmeans.labels_

        # x ir y reikšmių formatavimas grafiko naudojimui
        dataXAfter = []
        dataYAfter = []
        for a in kmeans.classifications:
            dataXAfter.append([])
            dataYAfter.append([])
            for classification in kmeans.classifications[a]:
                dataXAfter[a].append(classification[0])
                dataYAfter[a].append(classification[1])

        # dviejų grafikų piešimas
        fig, (ax0, ax1) = plot.subplots(nrows=1, ncols=2, figsize=(9, 5,))
        # grafikas skirtas neskirtytiems duomenims atvaizduoti
        ax0.scatter(dataUsingKey[keys[x_index]], dataUsingKey[keys[y_index]])
        # grafikai skirti atvaizduoti sugrupuotus duomenis
        if k_count > 1:
            ax1.scatter(dataXAfter[0], dataYAfter[0], c='b')
        if k_count > 1:
            ax1.scatter(dataXAfter[1], dataYAfter[1], c='g')
        if k_count > 2:
            ax1.scatter(dataXAfter[2], dataYAfter[2], c='r')
        if k_count > 3:
            ax1.scatter(dataXAfter[3], dataYAfter[3], c='y')
        if k_count > 4:
            ax1.scatter(dataXAfter[4], dataYAfter[4], c='m')
        if k_count > 5:
            ax1.scatter(dataXAfter[5], dataYAfter[5], c='c')
        ax1.set_xlabel(keys[x_index])
        ax1.set_ylabel(keys[y_index])
        ax0.set_xlabel(keys[x_index])
        ax0.set_ylabel(keys[y_index])
        ax0.set_title(" ")
        ax1.set_title(" ")
        fig.suptitle( keys[x_index] +" ir "+ keys[y_index] +" pasiskirstymas naudojant K-mean")

plot.tight_layout()
plot.show()

