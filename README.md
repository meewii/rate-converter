# Rate converter

### Description
The rate converter application shows a list of currencies got from Revolut's endpoint. Each row of the list displays the currency's name, code, flag and value.

The firt row of the list is the base currency - Euro is the default - which is set by the user and not part of the server response. When the user changes the base currency's value, the list of rates updates all the values simultaneously.
The user can change the base currency by clicking on one of the list items. The item becomes the base currency and the server calls are updated to return rates value depending on this new base currency.

### Stack
The app is using Kotlin, RxJava2, Dagger2, MVVM, Retrofit2, Mockito2 and Robolectric


### Bugs

- I am aware of the glitches linked to the list constanly refreshing while the input is focused. An easy fix would be to extract the base currency from the RecyclerView. 

- I am also aware of another strange bug: the soft keyboard's type changes when the user scrolls down. It is set by defaut to Number type, but changes to Text type. It is not possible to enter any text as the EditText only accepts numbers, but it is possible to make the app crash by clicking some of the text keys. I had no time to fix this issue yet. 

