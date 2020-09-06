from sklearn.metrics import silhouette_samples, silhouette_score
import matplotlib.pyplot as plt
import matplotlib.cm as cm
import numpy as np
import csv
import math

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

    print("bandymas nr. " + str(i) + " -----------------------")
    x_index = pairs[i][0]
    y_index = pairs[i][1]

    dataSorting = []
    coordinates = []
    data = []

    # sarašo paruošimas centroidų paieškai
    for a in range(len(dataUsingKey[keys[y_index]])):
        dataSorting.append([dataUsingKey[keys[x_index]][a], dataUsingKey[keys[y_index]][a]])
        data.append([dataUsingKey[keys[x_index]][a], dataUsingKey[keys[y_index]][a]])
    data = np.array(data)
    dataSorting.sort(key=sortBy)

    for n_clusters in range_n_clusters:

        # centroidu paieška atsižvelgiant į klasterių kiekį
        init_centroids = [[dataSorting[0][0], dataSorting[0][1]]]
        if n_clusters > 2:
            splitTo = int(len(dataSorting) / (n_clusters - 1))
            x = splitTo
            while x < splitTo * (n_clusters - 1):
                init_centroids.append([dataSorting[x][0], dataSorting[x][1]])
                x = x + splitTo

        init_centroids.append([dataSorting[len(dataSorting) - 1][0], dataSorting[len(dataSorting) - 1][1]])
        init_centroids = np.array(init_centroids)

        # sukuriamas grafikas su dviejais sub grafikais
        fig, (ax1, ax2) = plt.subplots(1, 2)
        fig.set_size_inches(18, 7)

        ax1.set_xlim([-0.1, 1])

        ax1.set_ylim([0, len(data) + (n_clusters + 1) * 10])

        # sukurto modelio panaudojimas
        clusterer = K_Means(n_clusters)
        clusterer.fit(data, init_centroids)
        cluster_labels = np.array(clusterer.labels_)

        # centroidu paruošimas naudojimu silhouette analizei
        centers = []
        for a in clusterer.centroids:
            centers.append([clusterer.centroids[a][0], clusterer.centroids[a][1]])
        centers = np.array(centers)

        # silhouette analizes panaudojimas įverčiui gauti
        silhouette_avg = silhouette_score(data, cluster_labels)
        print("For n_clusters =", n_clusters,
              "The average silhouette_score is :", silhouette_avg)

        # paskaičiuoti įvertį kiekvienam taškui
        sample_silhouette_values = silhouette_samples(data, cluster_labels)

        y_lower = 10
        for i in range(n_clusters):
            # agreguoti įvererčius naudojanmus pasirinktam klasteriui
            ith_cluster_silhouette_values = \
                sample_silhouette_values[cluster_labels == i]

            ith_cluster_silhouette_values.sort()

            size_cluster_i = ith_cluster_silhouette_values.shape[0]
            y_upper = y_lower + size_cluster_i

            color = cm.nipy_spectral(float(i) / n_clusters)
            ax1.fill_betweenx(np.arange(y_lower, y_upper), 0, ith_cluster_silhouette_values, facecolor=color, edgecolor=color, alpha=0.7)

            # legendos teksto sudarymas
            ax1.text(-0.05, y_lower + 0.5 * size_cluster_i, str(i))

            # paskaičiauoti nauja y apatini kuris naudojamas legendos tekstui
            y_lower = y_upper + 10

        ax1.set_title(" ")
        ax1.set_xlabel("The silhouette coefficient values")
        ax1.set_ylabel("Cluster label")

        # Raudona vertikali linija parodyti vidutiniam analizės įverčiui
        ax1.axvline(x=silhouette_avg, color="red", linestyle="--")

        ax1.set_yticks([])
        ax1.set_xticks([-0.1, 0, 0.2, 0.4, 0.6, 0.8, 1])

        # piešiamas antras grafikas pavaizduoti sugrupuotus taškus
        colors = cm.nipy_spectral(np.array(cluster_labels).astype(float) / n_clusters)
        ax2.scatter(data[:, 0], data[:, 1], marker='.', s=30, lw=0, alpha=0.7,
                    c=colors, edgecolor='k')

        # nupiešti centroido rutuliukus
        ax2.scatter(centers[:, 0], centers[:, 1], marker='o',
                    c="white", alpha=1, s=200, edgecolor='k')

        for i, c in enumerate(centers):
            ax2.scatter(c[0], c[1], marker='$%d$' % i, alpha=1,
                        s=50, edgecolor='k')

        ax2.set_title(" ")
        ax2.set_xlabel("Feature space for the 1st feature")
        ax2.set_ylabel("Feature space for the 2nd feature")

        plt.suptitle(("Silhouette analysis for KMeans clustering on sample data "
                      "with n_clusters = %d" % n_clusters),
                     fontsize=14, fontweight='bold')
    print("--------------------------------------")

plt.show()