def split(res):
    data = res["segments"]

    text = []
    for i in range(0, len(data)):
        text.append(data[i]["text"])

    speaker = []
    for i in range(0, len(data)):
        speaker.append(data[i]["speaker"]["label"])

    #words = []
    #for i in range(0, len(data)):
    #    words.append(data[i]["words"])

    temp_text = '/'.join(text)
    temp_speaker = '/'.join(speaker)
    data = temp_text + "///" + temp_speaker

    return data, text, speaker