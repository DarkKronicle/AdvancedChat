package io.github.darkkronicle.advancedchat.interfaces;

public interface RegistryOption<TYPE> {
    TYPE getOption();

    String getSaveString();
}