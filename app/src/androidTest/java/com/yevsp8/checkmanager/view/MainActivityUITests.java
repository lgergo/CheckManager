package com.yevsp8.checkmanager.view;


import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.yevsp8.checkmanager.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityUITests {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

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

    @Before
    public void setup() {
        Intents.init();
        if (doesViewExist("Most nem")) {
            ViewInteraction appCompatButton = onView(
                    allOf(withId(android.R.id.button2), withText("Most nem"),
                            childAtPosition(
                                    allOf(withClassName(is("android.widget.LinearLayout")),
                                            childAtPosition(
                                                    withClassName(is("android.widget.LinearLayout")),
                                                    3)),
                                    2),
                            isDisplayed()));
            appCompatButton.perform(click());
        }
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void mainActivity_UITest_elementsArePresent() {
        ViewInteraction view = onView(
                allOf(withId(R.id.toolbar_main),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        view.check(matches(isDisplayed()));

        ViewInteraction textView = onView(
                allOf(withText(R.string.checkList_title),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.checklist_fragmentcontainer),
                                        0),
                                0),
                        isDisplayed()));
        textView.check(matches(isDisplayed()));

        ViewInteraction textView2 = onView(
                allOf(withText(R.string.checkList_title),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.checklist_fragmentcontainer),
                                        0),
                                0),
                        isDisplayed()));
        textView2.check(matches(withText(R.string.checkList_title)));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.latest_synch),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                        1),
                                1),
                        isDisplayed()));
        textView3.check(matches(isDisplayed()));

        ViewInteraction imageButton = onView(
                allOf(withId(R.id.newImage_button),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        imageButton.check(matches(isDisplayed()));
    }

    @Test
    public void mainActivity_UITest_clickFloatingButton_navigatesToNewImageActivity() {

        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.newImage_button),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        floatingActionButton.perform(click());

        intended(hasComponent(NewImageActivity.class.getName()));

    }

    @Test
    public void clickMenuItem_home_navigatesTo_MainActivity() {
        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.menu_home), withContentDescription("Főoldal"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.toolbar_main),
                                        1),
                                0),
                        isDisplayed()));
        actionMenuItemView.perform(click());

        intended(hasComponent(MainActivity.class.getName()));
    }

    @Test
    public void clickMenuItem_settings_navigatesTo_SettingsActivity() {

        ViewInteraction actionMenuItemView2 = onView(
                allOf(withId(R.id.menu_settings), withContentDescription("Beállítások"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.toolbar_main),
                                        1),
                                1),
                        isDisplayed()));
        actionMenuItemView2.perform(click());

        intended(hasComponent(SettingsActivity.class.getName()));
    }

    @Test
    public void clickMenuItem_help_navigatesTo_HelpActivity() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.title), withText("Segítség"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.v7.view.menu.ListMenuItemView")),
                                        0),
                                0),
                        isDisplayed()));
        appCompatTextView.perform(click());

        intended(hasComponent(HelpActivity.class.getName()));
    }

    public boolean doesViewExist(String text) {
        try {
            onView(withText(text)).check(matches(isDisplayed()));
            return true;
        } catch (Throwable e) {
            return false;
        }
    }
}
