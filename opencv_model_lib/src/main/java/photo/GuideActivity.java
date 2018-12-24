package photo;
import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;


import com.cv_lib.opencv_model_lib.R;

import java.util.ArrayList;

public class GuideActivity extends ActivityGroup {

@Override
public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firstac);
        initViewPager();

        }
    private void initViewPager(){

        ViewPager viewPager = (ViewPager)findViewById(R.id.viewPager);
        View view1 = getLocalActivityManager().startActivity("activity01",

                new Intent(this, firststart.class)).getDecorView();

        View view2 = getLocalActivityManager().startActivity("activity02",

                new Intent(this, OpencvPhotoActivity.class)).getDecorView();




        ArrayList<View> views = new ArrayList<View>();

        views.add(view1);
        views.add(view2);



        MYViewPagerAdapter adapter = new MYViewPagerAdapter();

        adapter.setViews(views);

        viewPager.setAdapter(adapter);

    }



}




