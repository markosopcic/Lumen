package lumen.zpr.fer.hr.lumen;


import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import lumen.zpr.fer.hr.lumen.database.DBHelper;
import lumen.zpr.fer.hr.lumen.math.ProbabilityDistribution;


/**
 * Created by Kristijan on 8.11.2017.
 */

public class WordSupply {
    private DBHelper helper;
    //private List<Integer> wordIds;
    private String language;
    private Random rand;
    private ProbabilityDistribution wordProbDistr;
    private double PROBABILITY_SCALE_FACTOR = 0.8;
    int currentWordId;

    public WordSupply(Context context, String lang, String cat) {
        helper = new DBHelper(context);
        language = lang;

        wordProbDistr = new ProbabilityDistribution();
        for(int wordId: helper.getWordIds(lang, cat)) {
            wordProbDistr.addChoice(wordId);
        }
        rand = new Random();
        goToNext();
   }

    public LangDependentString getWord() {
        return new CroatianString(helper.getWord(currentWordId));
    }

    public String getImagePath() {
        return helper.getWordImagePath(currentWordId);
    }

    public String getWordRecordingPath() {
        return helper.getWordSoundPath(currentWordId);
    }

    public List<String> getLettersRecordingPaths() {
        LangDependentString letters = getWord().toUpperCase();

        List<String> paths = new ArrayList<>();

        for (int i=0;i<letters.length();i++){
            paths.add(helper.getLetterSoundPath(letters.charAt(i).toUpperCase(),language));
        }
        Log.d("PATHS",paths.toString());
        return paths;
    }

    public void goToNext() {
        double selection = rand.nextDouble();
        Log.d("DISTR","selection: "+Double.toString(selection));
        for(ProbabilityDistribution.DistributionInterval interval: wordProbDistr.getDistributionAsIntervalCollection()) {
            if(interval.getIntervalStart() <= selection && interval.getIntervalEnd() >= selection) {
                currentWordId = (int)interval.getIntervalChoice();
                Log.d("DISTR","selection id: "+currentWordId);
                wordProbDistr.increaseChoiceProbabilityByScaleFactor(interval.getIntervalChoice(), PROBABILITY_SCALE_FACTOR);
                return;
            }
        }
    }

}
