package com.example.tictac;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Button[][] buttons = new Button[3][3];
    private boolean playerXTurn = true; // Ход игрока X
    private int roundCount = 0; // Счетчик раундов
    private int scoreX = 0; // Счет игрока X
    private int scoreO = 0; // Счет игрока O
    private TextView scoreXTextView; // Отображение счета игрока X
    private TextView scoreOTextView; // Отображение счета игрока O
    private boolean isPlayingWithBot = false; // Флаг для режима игры с ботом
    private Button[] winningButtons = new Button[3]; // Массив для хранения выигравших кнопок
    private boolean gameEnded = false; // Флаг для отслеживания конца игры
    private View winningLine; // Линия для подсветки выигрышной комбинации

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Установка начального цвета фона
        getWindow().getDecorView().setBackgroundColor(Color.BLUE);

        // Инициализация элементов UI
        initializeUI();
        // Инициализация кнопок
        initializeButtons();
    }

    private void initializeUI() {
        scoreXTextView = findViewById(R.id.scoreX);
        scoreOTextView = findViewById(R.id.scoreO);
        winningLine = findViewById(R.id.winningLine); // Инициализация линии

        Button resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(v -> resetGame());

        Button playWithBotButton = findViewById(R.id.playWithBotButton);
        playWithBotButton.setOnClickListener(v -> playWithBot());

        Button playWithFriendButton = findViewById(R.id.playWithFriendButton);
        playWithFriendButton.setOnClickListener(v -> playWithFriend());
    }

    private void initializeButtons() {
        GridLayout gridLayout = findViewById(R.id.gridLayout);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String buttonID = "button" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[i][j] = findViewById(resID);
                buttons[i][j].setOnClickListener(new ButtonClickListener(i, j));
            }
        }
    }

    private class ButtonClickListener implements View.OnClickListener {
        private final int row;
        private final int col;

        public ButtonClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void onClick(View v) {
            // Проверка на занятость кнопки
            if (gameEnded || !((Button) v).getText().toString().equals("")) {
                Toast.makeText(MainActivity.this, "Эта кнопка уже занята!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Установка символа
            ((Button) v).setText(playerXTurn ? "X" : "O");
            roundCount++; // Увеличиваем счетчик после успешного хода

            if (checkForWinner()) {
                updateScore(); // Обновление счета
                showToast("Игрок " + (playerXTurn ? "X" : "O") + " победил!");
                highlightWinningButtons(); // Подсветка выигравших кнопок
                drawWinningLine(); // Рисуем линию для выигравшей комбинации
                gameEnded = true; // Установить флаг конца игры
            } else if (roundCount == 9) {
                showToast("Ничья!");
            } else {
                playerXTurn = !playerXTurn;
                if (isPlayingWithBot && !playerXTurn) {
                    playBot(); // Ход бота
                }
            }
        }
    }

    private void playBot() {
        Random random = new Random();
        int row, col;
        do {
            row = random.nextInt(3);
            col = random.nextInt(3);
        } while (!buttons[row][col].getText().toString().equals("")); // Поиск свободной ячейки

        buttons[row][col].setText("O");
        roundCount++;
        if (checkForWinner()) {
            scoreO++;
            scoreOTextView.setText("Игрок O: " + scoreO);
            showToast("Игрок O победил!");
            highlightWinningButtons(); // Подсветка выигравших кнопок
            drawWinningLine(); // Рисуем линию для выигравшей комбинации
            gameEnded = true; // Установить флаг конца игры
        } else if (roundCount == 9) {
            showToast("Ничья!");
        } else {
            playerXTurn = true; // Возвращаем ход игроку X
        }
    }

    private boolean checkForWinner() {
        // Проверка горизонталей
        for (int i = 0; i < 3; i++) {
            if (buttons[i][0].getText().equals(buttons[i][1].getText()) &&
                    buttons[i][0].getText().equals(buttons[i][2].getText()) &&
                    !buttons[i][0].getText().toString().equals("")) {
                winningButtons = new Button[]{buttons[i][0], buttons[i][1], buttons[i][2]};
                return true;
            }
        }
        // Проверка вертикалей
        for (int i = 0; i < 3; i++) {
            if (buttons[0][i].getText().equals(buttons[1][i].getText()) &&
                    buttons[0][i].getText().equals(buttons[2][i].getText()) &&
                    !buttons[0][i].getText().toString().equals("")) {
                winningButtons = new Button[]{buttons[0][i], buttons[1][i], buttons[2][i]};
                return true;
            }
        }
        // Проверка диагоналей
        if (buttons[0][0].getText().equals(buttons[1][1].getText()) &&
                buttons[0][0].getText().equals(buttons[2][2].getText()) &&
                !buttons[0][0].getText().toString().equals("")) {
            winningButtons = new Button[]{buttons[0][0], buttons[1][1], buttons[2][2]};
            return true;
        }
        if (buttons[0][2].getText().equals(buttons[1][1].getText()) &&
                buttons[0][2].getText().equals(buttons[2][0].getText()) &&
                !buttons[0][2].getText().toString().equals("")) {
            winningButtons = new Button[]{buttons[0][2], buttons[1][1], buttons[2][0]};
            return true;
        }
        return false;
    }

    private void highlightWinningButtons() {
        // Изменение фона только выигравших кнопок на желтый
        for (Button button : winningButtons) {
            button.setBackgroundColor(Color.YELLOW); // Подсветка выигравших кнопок
            button.setTextColor(Color.BLACK); // Изменение цвета текста на черный
        }
    }

    private void drawWinningLine() {
        // Установка видимости линии
        winningLine.setVisibility(View.VISIBLE);
        int[] position = new int[2]; // Массив для хранения координат

        // Получение координат выигравших кнопок
        winningButtons[0].getLocationOnScreen(position);
        int startX = position[0] + winningButtons[0].getWidth() / 2;
        int startY = position[1];

        winningButtons[2].getLocationOnScreen(position);
        int endX = position[0] + winningButtons[2].getWidth() / 2;
        int endY = position[1];

        // Вычисление координат и длины линии
        winningLine.setX(startX);
        winningLine.setY(startY + (winningButtons[0].getHeight() / 2) - (winningLine.getHeight() / 2)); // Центр линии по высоте
        winningLine.getLayoutParams().width = Math.abs(endX - startX); // Ширина линии
        winningLine.requestLayout(); // Применяем изменения
    }

    private void updateScore() {
        if (playerXTurn) {
            scoreX++;
            scoreXTextView.setText("Игрок X: " + scoreX);
        } else {
            scoreO++;
            scoreOTextView.setText("Игрок O: " + scoreO);
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void resetGame() {
        roundCount = 0; // Сбрасываем счетчик раундов
        playerXTurn = true; // Начинаем с игрока X
        gameEnded = false; // Сбросить флаг конца игры
        winningLine.setVisibility(View.GONE); // Скрыть линию
        for (Button[] row : buttons) {
            for (Button button : row) {
                button.setText("");
                button.setBackgroundColor(Color.BLUE); // Сбрасываем цвет фона кнопок
                button.setTextColor(Color.WHITE); // Сбрасываем цвет текста
            }
        }
        getWindow().getDecorView().setBackgroundColor(Color.BLUE); // Сбрасываем цвет фона
    }

    private void playWithBot() {
        resetGame();
        playerXTurn = true; // Начинаем с игрока X
        isPlayingWithBot = true; // Установить режим игры с ботом
    }

    private void playWithFriend() {
        resetGame();
        playerXTurn = true; // Начинаем с игрока X
        isPlayingWithBot = false; // Установить режим игры с другом
    }
}
