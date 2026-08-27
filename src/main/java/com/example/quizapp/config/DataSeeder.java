package com.example.quizapp.config;

import com.example.quizapp.entity.Category;
import com.example.quizapp.entity.Question;
import com.example.quizapp.entity.User;
import com.example.quizapp.repository.CategoryRepository;
import com.example.quizapp.repository.QuestionRepository;
import com.example.quizapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seeds the database with sample categories, questions, and users purely for
 * local testing / demoing the API. Runs only once (skips seeding if categories
 * already exist), and never restricts how many questions an admin can add later -
 * these are just starter records.
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        if (categoryRepository.count() > 0) {
            return; // already seeded
        }

        seedUsers();

        seedCategory("Animals", "Quiz questions related to animals", List.of(
                q("Which animal is known as the king of the jungle?", "Tiger", "Lion", "Elephant", "Leopard", "B"),
                q("Which is the largest land animal?", "Lion", "Elephant", "Tiger", "Giraffe", "B"),
                q("Which animal can change its color?", "Chameleon", "Elephant", "Horse", "Dog", "A"),
                q("Which bird cannot fly?", "Sparrow", "Eagle", "Ostrich", "Parrot", "C"),
                q("What is a baby dog called?", "Cub", "Kitten", "Puppy", "Calf", "C")
        ));

        seedCategory("Java", "Java programming quiz", List.of(
                q("Which keyword is used to inherit a class in Java?", "implements", "extends", "inherits", "super", "B"),
                q("Which method is the entry point of a Java program?", "start()", "run()", "main()", "init()", "C"),
                q("What is the default value of a boolean in Java?", "true", "false", "0", "null", "B"),
                q("Which collection class allows duplicate elements?", "Set", "Map", "List", "TreeSet", "C"),
                q("Which keyword is used to create an object in Java?", "new", "create", "object", "instance", "A")
        ));

        seedCategory("Python", "Python programming quiz", List.of(
                q("Which symbol is used for comments in Python?", "//", "#", "/*", "--", "B"),
                q("Which keyword defines a function in Python?", "func", "def", "function", "lambda", "B"),
                q("What is the output type of input() in Python?", "int", "str", "float", "bool", "B"),
                q("Which data structure is immutable in Python?", "list", "dict", "set", "tuple", "D"),
                q("Which library is widely used for data analysis in Python?", "NumPy", "Pandas", "React", "Django", "B")
        ));

        seedCategory("DBMS", "Database Management System quiz", List.of(
                q("What does SQL stand for?", "Structured Query Language", "Simple Query Language", "Sequential Query Language", "Standard Query Language", "A"),
                q("Which key uniquely identifies a record in a table?", "Foreign Key", "Primary Key", "Candidate Key", "Composite Key", "B"),
                q("Which command is used to remove a table structure and data?", "DELETE", "TRUNCATE", "DROP", "REMOVE", "C"),
                q("Which normal form removes transitive dependency?", "1NF", "2NF", "3NF", "BCNF", "C"),
                q("Which SQL clause is used to filter groups?", "WHERE", "HAVING", "GROUP BY", "ORDER BY", "B")
        ));

        seedCategory("Operating Systems", "Operating Systems quiz", List.of(
                q("Which scheduling algorithm can cause starvation?", "Round Robin", "FCFS", "Priority Scheduling", "SJF", "C"),
                q("What is the smallest unit of memory allocation in paging?", "Segment", "Page", "Frame", "Sector", "B"),
                q("Which of these is a deadlock prevention technique?", "Aging", "Banker's Algorithm", "Paging", "Segmentation", "B"),
                q("What does a semaphore do?", "Manages files", "Controls process synchronization", "Schedules CPU", "Manages memory", "B"),
                q("Which is a type of operating system?", "Batch OS", "Compiler", "Linker", "Assembler", "A")
        ));
    }

    private void seedUsers() {
        userRepository.save(User.builder()
                .name("Admin User")
                .email("admin@quizapp.com")
                .password("admin123")
                .role(User.Role.ADMIN)
                .build());

        userRepository.save(User.builder()
                .name("Demo User")
                .email("user@quizapp.com")
                .password("user123")
                .role(User.Role.USER)
                .build());
    }

    private void seedCategory(String name, String description, List<Question> questions) {
        Category category = categoryRepository.save(
                Category.builder().name(name).description(description).build());

        questions.forEach(question -> {
            question.setCategory(category);
            questionRepository.save(question);
        });
    }

    private Question q(String text, String a, String b, String c, String d, String correct) {
        return Question.builder()
                .questionText(text)
                .optionA(a)
                .optionB(b)
                .optionC(c)
                .optionD(d)
                .correctAnswer(correct)
                .build();
    }
}
