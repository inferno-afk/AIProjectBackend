package com.vasant.AIProjectBackend.entities;

public class AudioResult {
    private final String transcript;
    private final byte[] audio;

    public AudioResult(String transcript, byte[] audio) {
        this.transcript = transcript;
        this.audio = audio;
    }

    public String getTranscript() { return transcript; }
    public byte[] getAudio() { return audio; }
}
