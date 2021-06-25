/*
 * Copyright 2000-2018 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.flow.uitest.ui.routing;

import javax.servlet.http.HttpServletResponse;

import java.util.Collection;
import java.util.Optional;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.ElementFactory;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.RouteNotFoundError;
import com.vaadin.flow.uitest.ui.NonExitingImageView;

@ParentLayout(PushLayout.class)
public class PushRouteNotFoundView extends RouteNotFoundError {

    public static String PUSH_NON_EXISTENT_PATH = "push-no-route";

    private boolean isPushPath;

    @Override
    public int setErrorParameter(BeforeEnterEvent event,
            ErrorParameter<NotFoundException> parameter) {
        String path = event.getLocation().getPath();
        if (PUSH_NON_EXISTENT_PATH.equals(path)) {
            isPushPath = true;
            return HttpServletResponse.SC_NOT_FOUND;
        } else if (NonExitingImageView.IMAGE_NON_EXISTENT.equals(path)) {
            Collection<UI> uIs = event.getUI().getSession().getUIs();
            Optional<UI> viewUi = uIs.stream().filter(ui -> ComponentUtil
                    .getData(ui, NonExitingImageView.ID) != null).findFirst();
            if (viewUi.isPresent()) {
                viewUi.get().access(() -> {
                    Div div = new Div();
                    div.setId("not-found-invoked");
                    div.setText("RouteNotFoundError view is called");
                    viewUi.get().add(div);
                });
            }
            return super.setErrorParameter(event, parameter);
        } else {
            return super.setErrorParameter(event, parameter);
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        if (isPushPath) {
            Element div = ElementFactory.createDiv("Push mode: "
                    + attachEvent.getUI().getPushConfiguration().getPushMode());
            div.setAttribute("id", "push-mode");
            getElement().appendChild(div);
        }
    }
}
