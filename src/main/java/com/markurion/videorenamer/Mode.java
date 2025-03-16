package com.markurion.videorenamer;

/**
 * Enum for the mode of operation.
 */
public enum Mode {
    MODE1("Normal mode of operation"),
    MODE2("only (t=11:11 any thing you enter here) & title if (checkbox Add title on top) is checked");

    private final String description;

    Mode(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
};
