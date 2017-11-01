package ru.track.lambda;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 */
public class Streams {

    public static void main(String[] args) {
        List<Integer> ints = Arrays.asList(1, 2, 3, 4, 5, 6);

        List<Integer> dst = new ArrayList<>();

        // for
        for (Integer it : ints) {
            if (it % 2 == 0) {
                dst.add(it * it);
            }
        }
        System.out.println(dst);

        // streams
        dst = ints.stream()
                .filter((e) -> e % 2 == 0)
                .map((e) -> e * e)
                .collect(Collectors.toList());

        System.out.println(dst);


        List<User> users = Arrays.asList(new User(1, "A"), new User(2, "B"), new User(3, "C"));
        Map<Long, User> usersMap = users.stream()
                .collect(Collectors.toMap(
                        it -> it.id,
                        it -> it
                ));

        System.out.println(usersMap);


    }

    static class User {
        long id;
        String name;

        public User(long id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return "User{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}
