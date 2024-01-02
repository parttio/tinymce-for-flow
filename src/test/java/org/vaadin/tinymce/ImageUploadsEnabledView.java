package org.vaadin.tinymce;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.RequestHandler;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.server.VaadinSession;
import org.mockito.internal.matchers.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.firitin.components.RichText;
import org.vaadin.tinymce.imageuploads.ActiveEditors;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

@Route
public class ImageUploadsEnabledView extends Div {

    protected TinyMce tinyMce;

    @Autowired
    ActiveEditors activeEditors;

    public ImageUploadsEnabledView() {
        tinyMce = new TinyMce();
        tinyMce.configure("plugins", "code", "link", "image");
        tinyMce.configure("toolbar", "toolbar: 'undo redo | styleselect | bold italic | alignleft aligncenter alignright alignjustify | outdent indent' | link image");
        tinyMce.setValue("<p>Voi <strong>jorma</strong>!<p>");
        tinyMce.setHeight("700px");
        add(tinyMce);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        // Generate an id for this UI + use /upload rest
        // end point save uploaded/dragged images.
        // Id is only needed if you rest controller needs
        // to talk back to the UI somehow
        String id = activeEditors.register(tinyMce);
        tinyMce.configure("images_upload_url", "/upload/" + id);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        activeEditors.deRegister(tinyMce);
        super.onDetach(detachEvent);
    }
}
