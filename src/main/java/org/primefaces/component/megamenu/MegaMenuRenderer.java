/*
 * Copyright 2009-2012 Prime Teknoloji.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.component.megamenu;

import java.io.IOException;
import java.util.Iterator;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.component.menu.BaseMenuRenderer;
import org.primefaces.component.menu.Menu;
import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.component.separator.Separator;
import org.primefaces.component.submenu.Submenu;

public class MegaMenuRenderer extends BaseMenuRenderer {
    
    protected void encodeScript(FacesContext context, AbstractMenu abstractMenu) throws IOException{
		ResponseWriter writer = context.getResponseWriter();
        MegaMenu menu = (MegaMenu) abstractMenu;
		String clientId = menu.getClientId(context);
		
		startScript(writer, clientId);
        
        writer.write("PrimeFaces.cw('MegaMenu','" + menu.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
        writer.write("});");
        
		endScript(writer);        	
	}

    protected void encodeMarkup(FacesContext context, AbstractMenu abstractMenu) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        MegaMenu menu = (MegaMenu) abstractMenu;
		String clientId = menu.getClientId(context);
        String style = menu.getStyle();
        String styleClass = menu.getStyleClass();
        styleClass = styleClass == null ? MegaMenu.CONTAINER_CLASS : MegaMenu.CONTAINER_CLASS + " " + styleClass;

        writer.startElement("div", menu);
		writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if(style != null) {
            writer.writeAttribute("style", style, "style");
        }
        writer.writeAttribute("role", "menubar", null);

		writer.startElement("ul", null);
        writer.writeAttribute("class", Menu.LIST_CLASS, null);

		encodeRootItems(context, menu);
		
		writer.endElement("ul");

        writer.endElement("div");
	}
    
    protected void encodeRootItems(FacesContext context, MegaMenu menu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        for(Iterator<UIComponent> iterator = menu.getChildren().iterator(); iterator.hasNext();) {
            UIComponent child = (UIComponent) iterator.next();

            if(child.isRendered()) {
                if(child instanceof MenuItem) {
                    writer.startElement("li", null);
                    writer.writeAttribute("class", Menu.MENUITEM_CLASS, null);
                    writer.writeAttribute("role", "menuitem", null);
                    encodeMenuItem(context, (MenuItem) child);
                    writer.endElement("li");
                } 
                else if(child instanceof Submenu) {
                    Submenu submenu = (Submenu) child;
                    String style = submenu.getStyle();
                    String styleClass = submenu.getStyleClass();
                    styleClass = styleClass == null ? Menu.TIERED_SUBMENU_CLASS : Menu.TIERED_SUBMENU_CLASS + " " + styleClass;
        
                    writer.startElement("li", null);
                    writer.writeAttribute("class", styleClass, null);
                    if(style != null) {
                        writer.writeAttribute("style", style, null);
                    }
                    writer.writeAttribute("role", "menuitem", null);
                    writer.writeAttribute("aria-haspopup", "true", null);
                    
                    encodeRootSubmenu(context, submenu);
                    
                    writer.endElement("li");
                } 
                else if(child instanceof Separator) {
                    encodeSeparator(context, (Separator) child);
                }
            }
        }
    }

    protected void encodeRootSubmenu(FacesContext context, Submenu submenu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String icon = submenu.getIcon();
        String label = submenu.getLabel();

        //title
        writer.startElement("a", null);
        writer.writeAttribute("href", "javascript:void(0)", null);
        writer.writeAttribute("class", Menu.MENUITEM_LINK_CLASS, null);

        if(icon != null) {
            writer.startElement("span", null);
            writer.writeAttribute("class", Menu.MENUITEM_ICON_CLASS + " " + icon, null);
            writer.endElement("span");
        }

        if(label != null) {
            writer.startElement("span", null);
            writer.writeAttribute("class", Menu.MENUITEM_TEXT_CLASS, null);
            writer.writeText(submenu.getLabel(), "value");
            writer.endElement("span");
        }
        
        encodeTieredSubmenuIcon(context, submenu);

        writer.endElement("a");
        
        //submenus
		if(submenu.getChildCount() > 0) {
			writer.startElement("ul", null);
            writer.writeAttribute("class", Menu.TIERED_CHILD_SUBMENU_CLASS, null);
            writer.writeAttribute("role", "menu", null);
            
            for(Iterator<UIComponent> iterator = submenu.getChildren().iterator(); iterator.hasNext();) {
                UIComponent child = (UIComponent) iterator.next();

                if(child.isRendered()) {
                    writer.startElement("li", null);
                    
                    if(child instanceof Submenu) {
                        encodeDescendantSubmenu(context, (Submenu) child);
                    } 
                    else if(child instanceof Separator) {
                        encodeSeparator(context, (Separator) child);
                    }
                            
                    writer.endElement("li");
                }
            }

			writer.endElement("ul");
		}
    }

    protected void encodeDescendantSubmenu(FacesContext context, Submenu submenu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String label = submenu.getLabel();
        String style = submenu.getStyle();
        String styleClass = submenu.getStyleClass();
        styleClass = styleClass == null ? Menu.SUBMENU_TITLE_CLASS : Menu.SUBMENU_TITLE_CLASS + " " + styleClass;

        
        
        writer.startElement("ul", null);
        writer.writeAttribute("class", Menu.LIST_CLASS, null);
        writer.writeAttribute("role", "menu", null);
        
        //title
        writer.startElement("li", null);
        writer.writeAttribute("class", styleClass, null);
        if(style != null) {
            writer.writeAttribute("style", style, null);
        }
        
        writer.startElement("h3", null);
        if(label != null) {
            writer.writeText(label, "value");
        }
        writer.endElement("h3");
        
        writer.endElement("li");

        //menuitems
        for(Iterator<UIComponent> iterator = submenu.getChildren().iterator(); iterator.hasNext();) {
            UIComponent child = (UIComponent) iterator.next();

            if(child.isRendered()) {
                if(child instanceof MenuItem) {
                    writer.startElement("li", null);
                    writer.writeAttribute("class", Menu.MENUITEM_CLASS, null);
                    writer.writeAttribute("role", "menuitem", null);
                    encodeMenuItem(context, (MenuItem) child);
                    writer.endElement("li");
                } 
                else if(child instanceof Separator) {
                    encodeSeparator(context, (Separator) child);
                }
            }
        }
        
        writer.endElement("ul");
    }
}