package ru.track.lambda;

/**
 *
 */
public class Lambda {

    /**
     *
     * Single Abstract Method (SAM)
     *
     */
    @FunctionalInterface
    interface Listener {
        void onAction();
    }

    /*
     * v1
     */
    static class MyListener implements Listener {
        @Override
        public void onAction() {
            System.out.println("MyListener!");
        }
    }

    public static void main(String[] args) {
        Button button = new Button();
        button.addListener(new MyListener());

        button.addListener(new Listener() {
            @Override
            public void onAction() {
                System.out.println("Anonym!");
            }
        });
        button.addListener(() -> {
            System.out.println("Lambda!");
        });

        button.press();

    }
}
