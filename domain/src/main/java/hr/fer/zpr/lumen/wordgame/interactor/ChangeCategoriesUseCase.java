package hr.fer.zpr.lumen.wordgame.interactor;

import java.util.Set;

import hr.fer.zpr.lumen.wordgame.manager.WordGameManager;
import hr.fer.zpr.lumen.wordgame.model.Category;
import io.reactivex.Completable;

public class ChangeCategoriesUseCase implements CompletableUseCaseWithParams<Set<Category>> {

    private WordGameManager manager;

    public ChangeCategoriesUseCase(WordGameManager manager) {
        this.manager = manager;
    }

    @Override
    public Completable execute(Set<Category> categories) {
        return Completable.fromAction(()->manager.changeCategories(categories));
    }
}