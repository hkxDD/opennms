/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2015 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2015 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.netmgt.bsm.vaadin.adminpage;

import org.opennms.netmgt.bsm.service.BusinessServiceManager;
import org.opennms.netmgt.bsm.service.model.BusinessService;
import org.opennms.netmgt.bsm.service.model.IpService;
import org.opennms.netmgt.bsm.service.model.Status;
import org.opennms.netmgt.bsm.service.model.edge.Edge;
import org.opennms.netmgt.bsm.service.model.functions.map.Decrease;
import org.opennms.netmgt.bsm.service.model.functions.map.Identity;
import org.opennms.netmgt.bsm.service.model.functions.map.Ignore;
import org.opennms.netmgt.bsm.service.model.functions.map.Increase;
import org.opennms.netmgt.bsm.service.model.functions.map.SetTo;
import org.opennms.netmgt.bsm.service.model.mapreduce.MapFunction;
import org.opennms.netmgt.vaadin.core.TransactionAwareUI;
import org.opennms.netmgt.vaadin.core.UIHelper;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.vaadin.data.Validator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * Modal dialog window used to edit the properties of a Business Service Edge definition.
 *
 * @author Dustin Frisch <dustin@opennms.com>
 * @author Christian Pape <christian@opennms.org>
 */
public class BusinessServiceEdgeEditWindow extends Window {
    /**
     * the parent main layout
     */
    private BusinessServiceEditWindow m_businessServiceEditWindow;

    /**
     * declaring the components
     */
    private final NativeSelect m_typeSelect;
    private final ListSelect m_childServiceComponent;
    private final ListSelect m_ipServiceComponent;
    private final TextField m_reductionKeyComponent;
    private final NativeSelect m_mapFunctionSelect;
    private final NativeSelect m_mapFunctionSeveritySelect;
    private final TextField m_weightField;

    /**
     * Constructor
     *
     * @param businessService        the Business Service DTO instance to be configured
     * @param businessServiceManager the Business Service Manager
     */
    public BusinessServiceEdgeEditWindow(final BusinessService businessService,
                                         final BusinessServiceManager businessServiceManager) {
        super("Business Service Edge Edit");

        /**
         * Basic window setup
         */
        setModal(true);
        setClosable(false);
        setResizable(false);
        setWidth(50, Unit.PERCENTAGE);
        setHeight(75, Unit.PERCENTAGE);

        /**
         * Creating the root layout...
         */
        final VerticalLayout rootLayout = new VerticalLayout();
        rootLayout.setSizeFull();
        rootLayout.setSpacing(true);
        rootLayout.setMargin(false);

        /**
         * ...and the nested layout
         */
        final FormLayout formLayout = new FormLayout();
        formLayout.setSpacing(true);
        formLayout.setMargin(true);

        /**
         * type selector box
         */
        m_typeSelect = new NativeSelect("Type");
        m_typeSelect.setId("edgeTypeSelector");
        m_typeSelect.setMultiSelect(false);
        m_typeSelect.setNewItemsAllowed(false);
        m_typeSelect.setNullSelectionAllowed(false);
        m_typeSelect.setRequired(true);
        m_typeSelect.addItem(Edge.Type.CHILD_SERVICE);
        m_typeSelect.setItemCaption(Edge.Type.CHILD_SERVICE, "Child Service");
        m_typeSelect.addItem(Edge.Type.IP_SERVICE);
        m_typeSelect.setItemCaption(Edge.Type.IP_SERVICE, "IP Service");
        m_typeSelect.addItem(Edge.Type.REDUCTION_KEY);
        m_typeSelect.setItemCaption(Edge.Type.REDUCTION_KEY, "Reduction Key");
        m_typeSelect.setWidth(100.0f, Unit.PERCENTAGE);
        formLayout.addComponent(m_typeSelect);

        /**
         * child service list
         */
        m_childServiceComponent = new ListSelect("Child Service");
        m_childServiceComponent.setId("childServiceList");
        m_childServiceComponent.setMultiSelect(false);
        m_childServiceComponent.setNewItemsAllowed(false);
        m_childServiceComponent.setNullSelectionAllowed(false);
        m_childServiceComponent.setWidth(100.0f, Unit.PERCENTAGE);
        m_childServiceComponent.setRows(20);
        m_childServiceComponent.setVisible(false);
        m_childServiceComponent.setImmediate(true);
        m_childServiceComponent.setValidationVisible(true);
        m_childServiceComponent.addItems(businessServiceManager.getFeasibleChildServices(businessService));
        m_childServiceComponent.getItemIds().forEach(item -> m_childServiceComponent.setItemCaption(item, BusinessServiceEditWindow.describeBusinessService((BusinessService) item)));
        formLayout.addComponent(m_childServiceComponent);

        /**
         * ip service list
         */
        m_ipServiceComponent = new ListSelect("IP Service", businessServiceManager.getAllIpServices());
        m_ipServiceComponent.setId("ipServiceList");
        m_ipServiceComponent.setMultiSelect(false);
        m_ipServiceComponent.setNewItemsAllowed(false);
        m_ipServiceComponent.setNullSelectionAllowed(false);
        m_ipServiceComponent.setWidth(100.0f, Unit.PERCENTAGE);
        m_ipServiceComponent.setRows(20);
        m_ipServiceComponent.setVisible(false);
        m_ipServiceComponent.setImmediate(true);
        m_ipServiceComponent.setValidationVisible(true);
        m_ipServiceComponent.getItemIds().forEach(item -> m_ipServiceComponent.setItemCaption(item, BusinessServiceEditWindow.describeIpService((IpService) item)));
        formLayout.addComponent(m_ipServiceComponent);

        /**
         * reduction key input field
         */
        m_reductionKeyComponent = new TextField("Reduction Key");
        m_reductionKeyComponent.setId("reductionKeyField");
        m_reductionKeyComponent.setWidth(100.0f, Unit.PERCENTAGE);
        m_reductionKeyComponent.setVisible(false);
        m_reductionKeyComponent.setImmediate(true);
        m_reductionKeyComponent.setValidationVisible(true);
        formLayout.addComponent(m_reductionKeyComponent);

        /**
         * show and hide components
         */
        m_typeSelect.addValueChangeListener(event -> {
            m_childServiceComponent.setVisible(m_typeSelect.getValue() == Edge.Type.CHILD_SERVICE);
            m_childServiceComponent.setRequired(m_typeSelect.getValue() == Edge.Type.CHILD_SERVICE);
            m_ipServiceComponent.setVisible(m_typeSelect.getValue() == Edge.Type.IP_SERVICE);
            m_ipServiceComponent.setRequired(m_typeSelect.getValue() == Edge.Type.IP_SERVICE);
            m_reductionKeyComponent.setVisible(m_typeSelect.getValue() == Edge.Type.REDUCTION_KEY);
            m_reductionKeyComponent.setRequired(m_typeSelect.getValue() == Edge.Type.REDUCTION_KEY);
        });

        /**
         * map function field
         */
        m_mapFunctionSelect = new NativeSelect("Map Function",
                                               ImmutableList.builder()
                                                            .add(Decrease.class)
                                                            .add(Identity.class)
                                                            .add(Ignore.class)
                                                            .add(Increase.class)
                                                            .add(SetTo.class)
                                                            .build());
        m_mapFunctionSelect.setId("mapFunctionSelector");
        m_mapFunctionSelect.setNullSelectionAllowed(false);
        m_mapFunctionSelect.setMultiSelect(false);
        m_mapFunctionSelect.setNewItemsAllowed(false);
        m_mapFunctionSelect.setRequired(true);
        m_mapFunctionSelect.setWidth(100.0f, Unit.PERCENTAGE);

        /**
         * setting the captions for items
         */
        m_mapFunctionSelect.getItemIds().forEach(itemId -> m_mapFunctionSelect.setItemCaption(itemId, ((Class<?>) itemId).getSimpleName()));

        formLayout.addComponent(m_mapFunctionSelect);

        /**
         * severity selection field
         */
        m_mapFunctionSeveritySelect = new NativeSelect("Severity");
        m_mapFunctionSeveritySelect.setMultiSelect(false);
        m_mapFunctionSeveritySelect.setNewItemsAllowed(false);
        m_mapFunctionSeveritySelect.setNullSelectionAllowed(false);
        m_mapFunctionSeveritySelect.setRequired(false);
        m_mapFunctionSeveritySelect.addItem(Status.CRITICAL);
        m_mapFunctionSeveritySelect.setItemCaption(Status.CRITICAL, "Critical");
        m_mapFunctionSeveritySelect.addItem(Status.MAJOR);
        m_mapFunctionSeveritySelect.setItemCaption(Status.MAJOR, "Major");
        m_mapFunctionSeveritySelect.addItem(Status.MINOR);
        m_mapFunctionSeveritySelect.setItemCaption(Status.MINOR, "Minor");
        m_mapFunctionSeveritySelect.addItem(Status.WARNING);
        m_mapFunctionSeveritySelect.setItemCaption(Status.WARNING, "Warning");
        m_mapFunctionSeveritySelect.addItem(Status.NORMAL);
        m_mapFunctionSeveritySelect.setItemCaption(Status.NORMAL, "Normal");
        m_mapFunctionSeveritySelect.addItem(Status.INDETERMINATE);
        m_mapFunctionSeveritySelect.setItemCaption(Status.INDETERMINATE, "Indeterminate");
        m_mapFunctionSeveritySelect.setWidth(100.0f, Unit.PERCENTAGE);
        m_mapFunctionSeveritySelect.setEnabled(false);
        m_mapFunctionSeveritySelect.setImmediate(true);
        m_mapFunctionSeveritySelect.setValidationVisible(true);
        formLayout.addComponent(m_mapFunctionSeveritySelect);

        /**
         * hide or show additional severity input field
         */
        m_mapFunctionSelect.addValueChangeListener(event -> {
            m_mapFunctionSeveritySelect.setEnabled(SetTo.class.equals(m_mapFunctionSelect.getValue()));
            m_mapFunctionSeveritySelect.setRequired(SetTo.class.equals(m_mapFunctionSelect.getValue()));
        });

        /**
         * the weight input field
         */
        m_weightField = new TextField("Weight");
        m_weightField.setId("weightField");
        m_weightField.setRequired(true);
        m_weightField.setWidth(100.0f, Unit.PERCENTAGE);
        m_weightField.addValidator(value -> {
            try {
                Integer.parseInt((String) value);
            } catch (final NumberFormatException e) {
                throw new Validator.InvalidValueException("Weight must be a number");
            }
        });
        m_weightField.setImmediate(true);
        m_weightField.setValidationVisible(true);
        formLayout.addComponent(m_weightField);

        /**
         * setting the defaults
         */
        m_typeSelect.setValue(Edge.Type.CHILD_SERVICE);
        m_mapFunctionSelect.setValue(Identity.class);
        m_mapFunctionSeveritySelect.setValue(Status.INDETERMINATE);
        m_weightField.setValue(Integer.toString(Edge.DEFAULT_WEIGHT));

        /**
         * add the button layout...
         */
        final HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true);

        /**
         * ...and the save button
         */
        final Button saveButton = new Button("Add Edge");
        saveButton.setId("saveEdgeButton");
        saveButton.addClickListener(UIHelper.getCurrent(TransactionAwareUI.class).wrapInTransactionProxy((Button.ClickListener) event -> {
            if (!m_weightField.isValid()) return;
            if (!m_ipServiceComponent.isValid()) return;
            if (!m_childServiceComponent.isValid()) return;
            if (!m_reductionKeyComponent.isValid()) return;

            final MapFunction mapFunction;
            try {
                mapFunction = ((Class<? extends MapFunction>) m_mapFunctionSelect.getValue()).newInstance();
            } catch (final InstantiationException | IllegalAccessException e) {
                throw Throwables.propagate(e);
            }

            if (mapFunction instanceof SetTo) {
                ((SetTo) mapFunction).setStatus((Status) m_mapFunctionSeveritySelect.getValue());
            }

            final int weight = Integer.parseInt(m_weightField.getValue());

            switch ((Edge.Type) m_typeSelect.getValue()) {
                case CHILD_SERVICE:
                    businessService.addChildEdge((BusinessService) m_childServiceComponent.getValue(), mapFunction, weight);
                    break;

                case IP_SERVICE:
                    businessService.addIpServiceEdge((IpService) m_ipServiceComponent.getValue(), mapFunction, weight);
                    break;

                case REDUCTION_KEY:
                    businessService.addReductionKeyEdge(m_reductionKeyComponent.getValue(), mapFunction, weight);
                    break;
            }

            close();
        }));
        buttonLayout.addComponent(saveButton);

        /**
         * ...and a cancel button
         */
        final Button cancelButton = new Button("Cancel");
        cancelButton.setId("cancelEdgeButton");
        cancelButton.addClickListener((Button.ClickListener) event -> close());
        buttonLayout.addComponent(cancelButton);

        /**
         * now set the root layout
         */
        rootLayout.addComponent(formLayout);
        rootLayout.addComponent(buttonLayout);
        rootLayout.setComponentAlignment(buttonLayout, Alignment.BOTTOM_RIGHT);
        setContent(rootLayout);
    }
}