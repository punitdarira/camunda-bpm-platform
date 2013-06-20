package org.camunda.bpm.engine.impl.cmd;

import java.util.Collection;

import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;

/**
 * @author roman.smirnov
 * @author Joram Barrez
 */
public class RemoveExecutionVariablesCmd extends NeedsActiveExecutionCmd<Void> {

  private static final long serialVersionUID = 1L;

  private Collection<String> variableNames;
  private boolean isLocal;
  
  public RemoveExecutionVariablesCmd(String executionId, Collection<String> variableNames, boolean isLocal) {
    super(executionId);
    this.variableNames = variableNames;
    this.isLocal = isLocal;
  }
  
  protected Void execute(CommandContext commandContext, ExecutionEntity execution) {

    if (isLocal) {
      execution.removeVariablesLocal(variableNames);
    } else {
      execution.removeVariables(variableNames);
    }
    
    return null;
  }
  
  @Override
  protected String getSuspendedExceptionMessage() {
    return "Cannot remove variables because execution '" + executionId + "' is suspended";
  }

}
