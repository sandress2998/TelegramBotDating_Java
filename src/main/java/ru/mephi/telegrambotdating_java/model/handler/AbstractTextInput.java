package ru.mephi.telegrambotdating_java.model.handler;

abstract public class AbstractTextInput extends AbstractInput {
    protected abstract boolean isBelongToType(String text);
}
