package com.example.spring.ioc;

import java.util.ArrayList;
import java.util.List;

interface ClickListener {
    void onClick();
}

class Button {
    private final List<ClickListener> listeners = new ArrayList<>();

    void addListener(ClickListener listener) {
        listeners.add(listener);
    }

    void press() {
        System.out.println("[시스템] 버튼 눌림");

        for (ClickListener l : listeners) {
            l.onClick();
        }
    }
}

class LikeAction implements ClickListener {
    public void onClick() {
        System.out.println("좋아요 처리");
    }
}

class LogAction implements ClickListener {
    public void onClick() {
        System.out.println("클릭 로그 저장");
    }
}

public class Hollywood {
    public static void run() {
        Button button = new Button();
        button.addListener(new LikeAction());
        button.addListener(new LogAction());
        button.press();
    }
}
