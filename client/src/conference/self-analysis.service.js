export class SelfAnalysisService {
    getOnTimerEndedCallback() {
        return () => {
            console.log('timer ended');
        }
    }
}