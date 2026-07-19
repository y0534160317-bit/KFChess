package org.kfchess.view;


import com.fasterxml.jackson.annotation.JsonProperty;

public class AnimationConfig {
    public Physics physics;
    public Graphics graphics;


    public static class Physics {
        @JsonProperty("speed_m_per_sec") public double speed;
        @JsonProperty("next_state_when_finished") public String
                nextState;
    }



    public static class Graphics {
        @JsonProperty("frames_per_sec") public int frames_per_sec;
        @JsonProperty("is_loop") public boolean is_loop;

    }



}
