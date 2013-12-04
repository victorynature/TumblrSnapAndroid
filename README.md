# TumblrSnap

Tumblrsnap is a fun photos app built on Tumblr that supports an Instagram-like experience. With Tumlrsnap you can:

  * View photos from your tumblrsnap friends and explore others from the community
  * Select or take a photo, apply a fun filter and share it with the world
 
![My image1](/img/tumblr_photos.png)
![My image2](/img/tumblr_camera.png)
![My image3](/img/tumblr_taken_preview.png)
![My image4](/img/tumblr_gallery1.png)
![My image5](/img/tumblr_gallery2.png)
![My image6](/img/tumblr_gallery3.png)


## License

Apache Version 2.0


## Building

The build requires [Gradle](http://www.gradleware.com/)
v1.6 and the [Android SDK](http://developer.android.com/sdk/index.html)
to be installed in your development environment. In addition you'll need to set
the `ANDROID_HOME` environment variable to the location of your SDK:

    export ANDROID_HOME=/opt/tools/android-sdk

After satisfying those requirements, the build is pretty simple:

* Run `gradle assemble` from the root directory to build the APK only
* Run `gradle build` from the root directory to build the app and also run
  the integration tests, this requires a connected Android device or running
  emulator.

## Acknowledgements

This project uses the [v2 Tumblr API](http://www.tumblr.com/docs/en/api/v2) in order to manage all the images.

It also uses many other open source libraries such as:

 * [CodePath Rest-Client-Template](https://github.com/thecodepath/android-rest-client-template)
 * [scribe-java](https://github.com/fernandezpablo85/scribe-java)
 * [Android Async HTTP](https://github.com/loopj/android-async-http)
 * [codepath-oauth](https://github.com/thecodepath/android-oauth-handler)
 * [UniversalImageLoader](https://github.com/nostra13/Android-Universal-Image-Loader)
 * [ActiveAndroid](https://github.com/pardom/ActiveAndroid)
 * [RoboGuice](https://github.com/roboguice/roboguice)
 * [Gradle](https://github.com/gradle/gradle)




