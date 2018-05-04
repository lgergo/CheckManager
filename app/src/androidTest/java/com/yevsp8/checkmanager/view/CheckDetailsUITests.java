package com.yevsp8.checkmanager.view;

import android.content.Intent;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.yevsp8.checkmanager.R;
import com.yevsp8.checkmanager.util.Constants;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;


/**
 * Created by lgergo on 2018. 04. 22..
 */

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CheckDetailsUITests {

    @Rule
    public ActivityTestRule<CheckDetailsActivity> mActivityTestRule = new ActivityTestRule<>(CheckDetailsActivity.class, false, false);

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    @Test
    public void checkDetailsActivity_UITest_elementsArePresent() {
        Intent i = new Intent();
        String[] recognisedText = {"0", "1250", "bbb"};
        i.putExtra(Constants.RecognisedTextsArray, recognisedText);

        mActivityTestRule.launchActivity(i);

        ViewInteraction view = onView(
                allOf(withId(R.id.toolbar_checkDetails),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        view.check(matches(isDisplayed()));

        ViewInteraction textView = onView(
                allOf(withId(R.id.check_details_title), withText(R.string.title_activity_check_details),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.checkdetails_fragmentcontainer),
                                        0),
                                0),
                        isDisplayed()));
        textView.check(matches(withText(R.string.title_activity_check_details)));

        ViewInteraction linearLayout = onView(
                allOf(withId(R.id.check_details_id_label),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.checkdetails_fragmentcontainer),
                                        0),
                                1),
                        isDisplayed()));
        linearLayout.check(matches(isDisplayed()));

        ViewInteraction linearLayout2 = onView(
                allOf(withId(R.id.check_details_create_label),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.checkdetails_fragmentcontainer),
                                        0),
                                2),
                        isDisplayed()));
        linearLayout2.check(matches(isDisplayed()));

        ViewInteraction linearLayout3 = onView(
                allOf(withId(R.id.check_details_amount_label),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.checkdetails_fragmentcontainer),
                                        0),
                                3),
                        isDisplayed()));
        linearLayout3.check(matches(isDisplayed()));

        ViewInteraction linearLayout4 = onView(
                allOf(withId(R.id.check_details_paiddate_label),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.checkdetails_fragmentcontainer),
                                        0),
                                4),
                        isDisplayed()));
        linearLayout4.check(matches(isDisplayed()));

        ViewInteraction linearLayout5 = onView(
                allOf(withId(R.id.check_details_paidto_label),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.checkdetails_fragmentcontainer),
                                        0),
                                5),
                        isDisplayed()));
        linearLayout5.check(matches(isDisplayed()));

        ViewInteraction button = onView(
                allOf(withId(R.id.button_details_edit_save),
                        isDisplayed()));
        button.check(matches(isDisplayed()));

        ViewInteraction button2 = onView(
                allOf(withId(R.id.button_details_delete),
                        isDisplayed()));
        button2.check(matches(isDisplayed()));

        ViewInteraction button3 = onView(
                allOf(withId(R.id.button_details_upload),
                        isDisplayed()));
        button3.check(matches(isDisplayed()));
    }

    @Test
    public void checkDetailsActivity_clickUploadButton_navigatesToGoogleApiActivity() {

        Intent i = new Intent();
        String[] recognisedText = {"0", "1250", "bbb"};
        i.putExtra(Constants.RecognisedTextsArray, recognisedText);

        mActivityTestRule.launchActivity(i);

        ViewInteraction imageButton = onView(
                allOf(withId(R.id.button_details_upload),
                        isDisplayed()));
        imageButton.check(matches(isDisplayed()));

        intended(hasComponent(GoogleApiActivity.class.getName()));
    }
}
