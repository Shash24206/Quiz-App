import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizApp {

    private static Map<String, Quiz> quizzes = new HashMap<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quiz Application");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 400);

            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JButton createButton = new JButton("Create Quiz");
            createButton.addActionListener(e -> createQuiz(frame));
            mainPanel.add(createButton, BorderLayout.NORTH);

            JButton takeButton = new JButton("Take Quiz");
            takeButton.addActionListener(e -> takeQuiz(frame));
            mainPanel.add(takeButton, BorderLayout.CENTER);

            frame.add(mainPanel);
            frame.setVisible(true);
        });
    }

    private static void createQuiz(JFrame parentFrame) {
        JFrame createFrame = new JFrame("Create Quiz");
        createFrame.setSize(400, 300);
        createFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel nameLabel = new JLabel("Quiz Name:");
        JTextField nameField = new JTextField(20);
        panel.add(nameLabel);
        panel.add(nameField);

        JLabel numQuestionsLabel = new JLabel("Number of Questions:");
        JTextField numQuestionsField = new JTextField(5);
        panel.add(numQuestionsLabel);
        panel.add(numQuestionsField);

        JButton createButton = new JButton("Create");
        createButton.addActionListener(e -> {
            String quizName = nameField.getText().trim();
            if (quizName.isEmpty()) {
                JOptionPane.showMessageDialog(createFrame, "Quiz name cannot be empty.");
                return;
            }

            int numQuestions;
            try {
                numQuestions = Integer.parseInt(numQuestionsField.getText().trim());
                if (numQuestions <= 0) {
                    JOptionPane.showMessageDialog(createFrame, "Number of questions must be greater than zero.");
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(createFrame, "Please enter a valid number for number of questions.");
                return;
            }

            createQuizQuestions(createFrame, quizName, numQuestions);
        });
        panel.add(createButton);

        createFrame.add(panel);
        createFrame.setLocationRelativeTo(parentFrame);
        createFrame.setVisible(true);
    }

    private static void createQuizQuestions(JFrame parentFrame, String quizName, int numQuestions) {
        Quiz quiz = new Quiz(quizName);

        JFrame questionsFrame = new JFrame("Add Questions to " + quizName);
        questionsFrame.setSize(600, 400);
        questionsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (int i = 0; i < numQuestions; i++) {
            JPanel questionPanel = new JPanel(new BorderLayout());
            questionPanel.setBorder(BorderFactory.createEtchedBorder());

            JLabel questionLabel = new JLabel("Question " + (i + 1) + ":");
            JTextField questionField = new JTextField(30);
            questionPanel.add(questionLabel, BorderLayout.WEST);
            questionPanel.add(questionField, BorderLayout.CENTER);

            JLabel numChoicesLabel = new JLabel("Number of Choices:");
            JTextField numChoicesField = new JTextField(5);
            questionPanel.add(numChoicesLabel, BorderLayout.SOUTH);
            questionPanel.add(numChoicesField, BorderLayout.SOUTH);

            panel.add(questionPanel);

            JButton addQuestionButton = new JButton("Add Question");
            int finalI = i;
            addQuestionButton.addActionListener(e -> {
                String questionText = questionField.getText().trim();
                int numChoices;
                try {
                    numChoices = Integer.parseInt(numChoicesField.getText().trim());
                    if (numChoices <= 0) {
                        JOptionPane.showMessageDialog(questionsFrame, "Number of choices must be greater than zero.");
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(questionsFrame, "Please enter a valid number for number of choices.");
                    return;
                }

                createQuestion(quiz, questionText, numChoices, finalI);
            });
            panel.add(addQuestionButton);
        }

        JButton finishButton = new JButton("Finish");
        finishButton.addActionListener(e -> {
            if (quiz.getNumQuestions() == numQuestions) {
                quizzes.put(quizName, quiz);
                JOptionPane.showMessageDialog(questionsFrame, "Quiz created successfully.");
                questionsFrame.dispose();
            } else {
                JOptionPane.showMessageDialog(questionsFrame, "Please add all questions before finishing.");
            }
        });
        panel.add(finishButton);

        questionsFrame.add(panel);
        questionsFrame.setLocationRelativeTo(parentFrame);
        questionsFrame.setVisible(true);
    }

    private static void createQuestion(Quiz quiz, String questionText, int numChoices, int questionIndex) {
        JFrame questionFrame = new JFrame("Add Choices for Question " + (questionIndex + 1));
        questionFrame.setSize(400, 300);
        questionFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel questionLabel = new JLabel("Question " + (questionIndex + 1) + ": " + questionText);
        panel.add(questionLabel);

        for (int i = 0; i < numChoices; i++) {
            JPanel choicePanel = new JPanel();

            JLabel choiceLabel = new JLabel("Choice " + (i + 1) + ":");
            JTextField choiceField = new JTextField(20);
            choicePanel.add(choiceLabel);
            choicePanel.add(choiceField);

            panel.add(choicePanel);
        }

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            List<String> choices = new ArrayList<>();
            int correctChoiceIndex = 0; // Defaulting to the first choice as correct

            Component[] components = panel.getComponents();
            for (Component component : components) {
                if (component instanceof JPanel) {
                    JPanel choicePanel = (JPanel) component;
                    JTextField choiceField = (JTextField) choicePanel.getComponent(1);
                    choices.add(choiceField.getText().trim());
                }
            }

            quiz.addQuestion(new Question(questionText, choices, correctChoiceIndex));
            JOptionPane.showMessageDialog(questionFrame, "Question added successfully.");
            questionFrame.dispose();
        });
        panel.add(submitButton);

        questionFrame.add(panel);
        questionFrame.setVisible(true);
    }

    private static void takeQuiz(JFrame parentFrame) {
        String quizName = JOptionPane.showInputDialog(parentFrame, "Enter the name of the quiz:");
        if (quizName == null || quizName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, "Quiz name cannot be empty.");
            return;
        }

        Quiz quiz = quizzes.get(quizName);
        if (quiz == null) {
            JOptionPane.showMessageDialog(parentFrame, "Quiz not found.");
            return;
        }

        int score = 0;
        for (int i = 0; i < quiz.getNumQuestions(); i++) {
            Question question = quiz.getQuestion(i);
            StringBuilder message = new StringBuilder("Question " + (i + 1) + ": " + question.getQuestion() + "\n");
            for (int j = 0; j < question.getChoices().size(); j++) {
                message.append((j + 1)).append(": ").append(question.getChoices().get(j)).append("\n");
            }
            String userAnswer = JOptionPane.showInputDialog(parentFrame, message.toString() + "Enter your answer:");
            if (userAnswer != null && Integer.parseInt(userAnswer) - 1 == question.getCorrectChoice()) {
                JOptionPane.showMessageDialog(parentFrame, "Correct!");
                score++;
            } else {
                JOptionPane.showMessageDialog(parentFrame, "Incorrect. The correct answer is " + (question.getCorrectChoice() + 1) + ".");
            }
        }

        JOptionPane.showMessageDialog(parentFrame, "Your score is " + score + " out of " + quiz.getNumQuestions() + ".");
    }
}

class Quiz {
    private String name;
    private List<Question> questions = new ArrayList<>();

    public Quiz(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addQuestion(Question question) {
        questions.add(question);
    }

    public Question getQuestion(int index) {
        return questions.get(index);
    }

    public int getNumQuestions() {
        return questions.size();
    }
}

class Question {
    private String question;
    private List<String> choices;
    private int correctChoice;

    public Question(String question, List<String> choices, int correctChoice) {
        this.question = question;
        this.choices = choices;
        this.correctChoice = correctChoice;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getChoices() {
        return choices;
    }

    public int getCorrectChoice() {
        return correctChoice;
    }
}
