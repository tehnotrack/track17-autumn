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
        void onAction(String cmd);
    }

    /*
     * v1
     */
    static class MyListener implements Listener {
        @Override
        public void onAction(String cmd) {
            System.out.println("MyListener!");
        }
    }

    public static void main(String[] args) {
        Button button = new Button();
        button.addListener(new MyListener());

        button.addListener(new Listener() {
            @Override
            public void onAction(String cmd) {
                System.out.println("Anonym!");
            }
        });


        button.addListener((cmd) -> System.out.println("Lambda!"));

        button.press();

    }
}
