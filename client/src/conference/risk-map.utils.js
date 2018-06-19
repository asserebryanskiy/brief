import $ from "jquery";

export class RiskMapUtils {

    static getAnswerStr() {
        let answerStr = '';
        $('#risk-map-phase .risk-img-cell').each((i, el) => {
            const $indicator = $(el).find('.risk-indicator');
            if ($indicator.hasClass('no-level')) answerStr += i + '-0,';
            if ($indicator.hasClass('low-level')) answerStr += i + '-1,';
            if ($indicator.hasClass('mid-level')) answerStr += i + '-2,';
            if ($indicator.hasClass('high-level')) answerStr += i + '-3,';

            // delete last comma
            if (i === 11 && answerStr.length > 0) answerStr = answerStr.substr(0, answerStr.length - 1);
        });
        return answerStr;
    }

    static getAnswerMatrix(answer) {
        const answerMatrix = new Array(3);
        for (let i = 0; i < 3; i++) {
            const inner = new Array(4);
            inner.fill(-1);
            answerMatrix[i] = inner;
        }
        if (answer.length === 0) return answerMatrix;
        let acc = '';
        let sector = 0;
        for (let i = 0; i < answer.length; i++) {
            const letter = answer.charAt(i);
            if (letter === '-') {
                sector = parseInt(acc);
                acc = '';
            } else if (letter === ',') {
                answerMatrix[Math.floor(sector / 4)][sector % 4] = parseInt(acc);
                acc = '';
                sector = 0;
            } else {
                acc += letter;
            }
        }
        answerMatrix[Math.floor(sector / 4)][sector % 4] = parseInt(acc);
        return answerMatrix;
    }

    static getTotalScore(answerMatrix) {
        let score = 0;
        for (let i = 0; i < 3; i++) {
            for (let j = 0; j < 4; j++) {
                score += RiskMapUtils.getScoreForSector(i, j, answerMatrix[i][j])
            }
        }

        return score;
    }

    static getScoreForSector(row, column, answer) {
        const correctAnswers = [
            [-1,1,1,-1],
            [3,1,1,1],
            [2,1,1,-1],
        ];

        // scoring varies depending on correct answer
        switch (correctAnswers[row][column]) {
            case -1: return 0;
            case 0:
                switch (answer) {
                    case -1: return -100;
                    case 0: return 100;
                    case 1: return 50;
                    case 2: return 25;
                    case 3: return 0;
                }
                break;
            case 1:
                switch (answer) {
                    case -1: return -200;
                    case 0: return 50;
                    case 1: return 200;
                    case 2: return 100;
                    case 3: return 50;
                }
                break;
            case 2:
                switch (answer) {
                    case -1: return -300;
                    case 0: return 25;
                    case 1: return 100;
                    case 2: return 300;
                    case 3: return 150;
                }
                break;
            case 3:
                switch (answer) {
                    case -1: return -400;
                    case 0: return 0;
                    case 1: return 50;
                    case 2: return 150;
                    case 3: return 400;
                }
                break;
        }
    }
}