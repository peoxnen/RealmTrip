package iview.wsienski.realm;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = (TextView) findViewById(R.id.textView);

        Dog dog = new Dog();
        dog.setName("Rex");
        dog.setAge(1);

        Realm.init(this);
        Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();
        realm.copyToRealm(dog);
        realm.commitTransaction();

        Dog dogFromDB = realm.where(Dog.class).greaterThan("age", 0).findFirst();
        int size = realm.where(Dog.class).findAll().size();
        textView.setText(dogFromDB.getName() + " size=" +size);


    }

}
