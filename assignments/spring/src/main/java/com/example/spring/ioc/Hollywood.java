package com.example.spring.ioc;

interface ClickListener {
    void onClick();
}

class Button {
    private ClickListener listener;

    void setListener(ClickListener listener) {
        this.listener = listener;
    }

    void press() {
        System.out.println("[시스템] 버튼 눌림");
        listener.onClick();
    }
}

class LikeAction implements ClickListener {
    public void onClick() {
        System.out.println("내 코드 실행: 굿!");
    }
}

public class Hollywood {
    public static void run() {
        Button button = new Button();
        button.setListener(new LikeAction());
        button.press();
    }
}
