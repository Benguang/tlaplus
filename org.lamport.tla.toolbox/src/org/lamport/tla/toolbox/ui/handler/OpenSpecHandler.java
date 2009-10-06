package org.lamport.tla.toolbox.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.part.FileEditorInput;
import org.lamport.tla.toolbox.Activator;
import org.lamport.tla.toolbox.spec.Spec;
import org.lamport.tla.toolbox.spec.nature.ParserHelper;
import org.lamport.tla.toolbox.ui.perspective.InitialPerspective;
import org.lamport.tla.toolbox.ui.perspective.SpecLoadedPerspective;
import org.lamport.tla.toolbox.util.UIHelper;

/**
 * Handles the open-spec command
 * @author Simon Zambrovski
 * @version $Id$
 */
public class OpenSpecHandler extends AbstractHandler implements IHandler
{
    public static final String TLA_EDITOR_CURRENT = "org.lamport.tla.toolbox.editor.basic.TLAEditorAndPDFViewer";

    public static final String TLA_EDITOR = TLA_EDITOR_CURRENT;
    public static final String COMMAND_ID = "toolbox.command.spec.open";
    public static final String PARAM_SPEC = "toolbox.command.spec.open.param";

    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        String specName = event.getParameter(PARAM_SPEC);
        // if no spec name, exit
        if (specName == null)
        {
            return null;
        }

        // if another spec is currently loaded, close it
        if (Activator.getSpecManager().getSpecLoaded() != null)
        {
            UIHelper.runCommand(CloseSpecHandler.COMMAND_ID, null);
        }

        final Spec spec = Activator.getSpecManager().getSpecByName(specName);
        if (spec == null)
        {
            throw new RuntimeException("Specification " + specName + "not found");
        }

        // spec the perspective
        UIHelper.switchPerspective(SpecLoadedPerspective.ID);
        // close the initial perspective
        UIHelper.closeWindow(InitialPerspective.ID);

        // store information about opened spec in the spec manager
        Activator.getSpecManager().setSpecLoaded(spec);

        // open the editor
        UIHelper.openEditor(TLA_EDITOR, new FileEditorInput(spec.getRootFile()));

        // rebuild current spec
        ParserHelper.rebuildSpec(new NullProgressMonitor());

        return null;
    }

}
