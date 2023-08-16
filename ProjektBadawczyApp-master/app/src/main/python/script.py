import os
import librosa

def main(pathToVoice, samplePath):
    #path = os.path.join(os.path.dirname(__file__), pathToVoice[0])
    path = pathToVoice[0]
    print("-------------")
    print(path)
    print("-------------")
    a, b = librosa.load(path)
    return True