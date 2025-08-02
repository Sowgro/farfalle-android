package net.sowgro.farfalle;

import androidx.databinding.Observable;
import androidx.databinding.ObservableField;

public class Util {

    public interface ConnectCallback<T>{ void set(T value); }

    /**
     * Connects an observable field to a setter
     * It sets the current value then listens for and sets future values
     * @param field The observable field
     * @param setter The setter function
     * @param <T> The type of the observable value
     */
    public static <T> void connect(ObservableField<T> field, ConnectCallback<T> setter) {
        setter.set(field.get());
        field.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                setter.set(field.get());
            }
        });
    }
}
