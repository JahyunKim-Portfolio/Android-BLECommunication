import os
import math
import matplotlib.pyplot as plt

def read_file(fname):
    with open(fname, "rb") as file:
        lines = file.readlines()
    return lines

def load_data(fname):
    data = dict()
    lines = read_file(fname)
    for line in lines:
        col = line.split(b" ")
        data[col[0]] = int(col[1])
    return data

def sigmoid(x):
  return 1 / (1 + math.exp(-x))

def norm(rssi_arr):
    norm_arr = []
    for rssi in rssi_arr:
        norm_arr.append(sigmoid(-1 * (rssi+30)/20))
    return norm_arr

def distance(vec1, vec2):
    sum = 0
    for x in range(len(vec1)):
        sum += (vec1[x] - vec2[x])*((vec1[x] - vec2[x]))
    return math.sqrt(sum)

def get_similarity(vec1, vec2):
    print(vec1)
    print(vec2)
    vec1, vec2 = norm(vec1), norm(vec2)
    print(vec1)
    print(vec2)
    print(distance(vec1, vec2))
    print()
    return distance(vec1, vec2)

def draw_graph(similarity_arr):
    plt.plot(similarity_arr)
    plt.ylim([0, 1])
    plt.show()


if __name__ == "__main__":
    DATA_PATH = os.path.join(".","data")
    EXPERIMENTS = ["Test1", "Test2", "Test3"]
    USERS = ["1", "2"]
    for experiment in EXPERIMENTS:
        experiment_dir = os.path.join(DATA_PATH, experiment)
        data = dict()
        for user in USERS:
            user_dir = os.path.join(experiment_dir, user)
            sample = 0
            data[user] = []
            while(os.path.isfile(os.path.join(user_dir, str(sample) + ".txt"))):
                data[user].append(load_data(os.path.join(user_dir, str(sample) + ".txt")))
                sample += 1
        similarity_arr = []
        # print("hi ")
        # print(data[USERS[0]])
        for sample in range(len(data[USERS[0]])):
            devices = set()
            vec_arr = dict()
            for user in USERS:
                for key in data[user][sample].keys():
                    devices.add(key)
            for user in USERS:
                vec_arr[user] = []
                for device in devices:
                    if device not in data[user][sample] or data[user][sample][device] < -90:
                        vec_arr[user].append(-90)
                    else:
                        vec_arr[user].append(data[user][sample][device])

            similarity_arr.append(get_similarity(vec_arr[USERS[0]], vec_arr[USERS[1]]))
        draw_graph(similarity_arr)
