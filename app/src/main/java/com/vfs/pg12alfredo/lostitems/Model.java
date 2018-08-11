package com.vfs.pg12alfredo.lostitems;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

// Idea from amrro's answer at: https://stackoverflow.com/questions/46995080/how-do-i-get-the-document-id-for-a-firestore-document-using-kotlin-data-classes
// This basically says that any property passed onto the class instance that does not map to any class fields will be ignored
@IgnoreExtraProperties
public class Model {

    // We want the id excluded from the database
    // This is here so that I don't need to have an array of items and an array on item ids passed onto the recycler adapter
    // I can just access the id via the item model if I want to update or delete something in this way
    // ...because the item will inherit from this.
    @Exclude
    public String id;

    // This will basically allow me to pass an id to anything that inherits from Model from a document in a FireStore query
    // I restrict the type to model
    <T extends Model> T withId(String id) {
        this.id = id;
        return (T) this; // Cast the instance to the generic type, which can be anything that inherits from Model
    }


}
