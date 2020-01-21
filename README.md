# Rate converter

### Description
The rate converter application shows a list of currencies got from Revolut's endpoint. Each row of the list displays the currency's name, code, flag and value.

The firt row of the list is the base currency - Euro is the default - which is set by the user and not part of the server response. When the user changes the base currency's value, the list of rates updates all the values simultaneously.
The user can change the base currency by clicking on one of the list items. The item becomes the base currency and the server calls are updated to return rates value depending on this new base currency.

### Stack
Kotlin, RxJava, Dagger, MVVM, Retrofit, Mockito, Robolectric
