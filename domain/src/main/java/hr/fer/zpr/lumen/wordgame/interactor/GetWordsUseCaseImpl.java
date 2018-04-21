package hr.fer.zpr.lumen.wordgame.interactor.word;

import java.util.List;
import java.util.Set;

import hr.fer.zpr.lumen.wordgame.model.Category;
import hr.fer.zpr.lumen.wordgame.model.Language;
import hr.fer.zpr.lumen.wordgame.model.Word;
import hr.fer.zpr.lumen.wordgame.repository.WordGameRepository;
import io.reactivex.Single;

public class GetWordsUseCaseImpl implements SingleUseCaseWIthParams<GetWordsUseCaseImpl.WordAttributes,List<Word>>{

    private final WordGameRepository repository;

    public GetWordsUseCaseImpl(WordGameRepository repository){
        this.repository=repository;
    }

    @Override
    public Single<List<Word>> execute(WordAttributes attributes) {
        return repository.getWordsFromCategories(attributes.categories, attributes.language);
    }

    public static class WordAttributes{
       public Set<Category> categories;
       public  Language language;

       public WordAttributes(Set<Category> categories,Language language){
           this.categories=categories;
           this.language=language;
       }
    }
}
