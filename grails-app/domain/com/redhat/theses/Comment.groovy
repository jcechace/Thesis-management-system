package com.redhat.theses

import com.redhat.theses.auth.User

/**
 * @author vdedik@redhat.com
 */
class Comment {
    String content
    Date dateCreated
    User user
    Article article

    static constraints = {
        content widget: 'textarea', nullable: false, blank: false
        article nullable: false
        user nullable: false
    }

    static mapping = {
        content type: 'text'
    }

    String toString() {
        content
    }
}
