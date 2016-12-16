package iview.wsienski.realm;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv_dog_number)
    TextView dogNumber;
    @BindView(R.id.new_name)
    TextView newName;
    @BindView(R.id.new_age)
    TextView newAge;
    @BindView(R.id.the_oldest_dog_name)
    TextView theOldestDogName;
    @BindView(R.id.the_oldest_dog_age)
    TextView theOldestDogAge;

    Realm realm;
    RealmResults<Dog> dogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Realm.init(this);
        realm = Realm.getDefaultInstance();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        dogs = realm.where(Dog.class).findAll();
        updateInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dogs.addChangeListener(new RealmChangeListener<RealmResults<Dog>>() {
            @Override
            public void onChange(RealmResults<Dog> results) {
                updateInfo();
            }
        });
    }

    @OnClick(R.id.btn_add_dog)
    public void addDog(View view) {
        String name = getNewName();
        Integer age = getNewAge();
        if(!TextUtils.isEmpty(name) && age>0) {
            Dog dog = new Dog(name, age);
            Timber.d("addDog: name=" + name + " age=" + age);
            saveDogInDB(dog);
            updateInfo();
        }else{
            Toast.makeText(this, "Wrong input data!", Toast.LENGTH_LONG).show();
        }
    }

    public String getNewName() {
        return newName.getText().toString();
    }

    public int getNewAge() {
        int age=0;
        try{
            age=Integer.valueOf(newAge.getText().toString());
        }catch (NumberFormatException e){
            e.printStackTrace();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        return age;
    }

    void saveDogInDB(Dog dog){
        realm.beginTransaction();
        realm.copyToRealm(dog);
        realm.commitTransaction();
    }

    void updateInfo(){
        long number = realm.where(Dog.class).count();
        if(number>0) {
            updateDogInfo();
        }
    }

    private void updateDogInfo() {
        Dog dogFromDB = realm.where(Dog.class).findAllSorted("age", Sort.DESCENDING).first();
        Timber.d("updateDogInfo: name="+dogFromDB.getName()+" age="+dogFromDB.getAge());
        setTheOldestDog(dogFromDB.getName(), dogFromDB.getAge());
    }

    public void setDogNumber(long number) {
        dogNumber.setText(String.valueOf(number));
    }

    public void setTheOldestDog(String name, int age){
        theOldestDogName.setText(name);
        theOldestDogAge.setText(String.valueOf(age));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
