package net.darkkronicle.advancedchat.interfaces;

public interface RegistryOption<TYPE> {
    TYPE getOption();

    String getSaveString();
}