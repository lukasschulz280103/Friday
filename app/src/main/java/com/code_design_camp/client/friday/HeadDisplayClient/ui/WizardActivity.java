package com.code_design_camp.client.friday.HeadDisplayClient.ui;

import android.os.Bundle;

import com.code_design_camp.client.friday.HeadDisplayClient.R;
import com.code_design_camp.client.friday.HeadDisplayClient.fragments.DefaultSigninFragment;
import com.code_design_camp.client.friday.HeadDisplayClient.fragments.wizard.AcceptTerms;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;


public class WizardActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showSkipButton(false);
        showStatusBar(false);

        SliderPage welcomePage = new SliderPage();
        welcomePage.setTitle(getString(R.string.app_welcome));
        welcomePage.setDescription(getString(R.string.wizard_welcome_descr));
        welcomePage.setImageDrawable(R.mipmap.ic_launcher);

        SliderPage project_info_page = new SliderPage();
        project_info_page.setTitle(getString(R.string.project_friday));
        project_info_page.setDescription(getString(R.string.app_friday_description));

        addSlide(AppIntroFragment.newInstance(welcomePage));
        addSlide(AppIntroFragment.newInstance(project_info_page));
        addSlide(AcceptTerms.newInstance());
    }
}
