package hr.fer.zpr.lumen.wordgame.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public final class Word {

    public final Language language;
    public final Set<Category> categories;
    public final String stringValue;
    public final Image wordImage;
    public final Sound sound;
    public List<Letter> letters;


    public Word(String word, Language language, Sound sound, Collection<Category> categories, Image image, List<Letter> letters) {
        this.language = language;
        this.categories = new HashSet<>(categories);
        this.stringValue = word;
        this.wordImage = image;
        this.letters = letters;
        this.sound = sound;

    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Word)) return false;
        Word word = (Word) o;
        if (!this.stringValue.equals(word.stringValue)) return false;
        if (this.language != word.language) return false;
        return true;
    }

    public void setLetters(List<Letter> letters) {
        this.letters = letters;
    }
}
