package gestionlibreria.ui.swing.model;

import gestionlibreria.model.Categoria;

import javax.swing.AbstractListModel;
import java.util.ArrayList;
import java.util.List;

/**
 * ListModel personalizado para mostrar categorías en un {@code JList}.
 * También utiliza sobreescritura de métodos (SIA2.7).
 */
public class CategoriaListModel extends AbstractListModel<Categoria> {
    private final List<Categoria> categorias = new ArrayList<>();

    public void setCategorias(List<Categoria> nuevasCategorias) {
        categorias.clear();
        if (nuevasCategorias != null) {
            categorias.addAll(nuevasCategorias);
        }
        int ultimo = Math.max(0, categorias.size() - 1);
        fireContentsChanged(this, 0, ultimo);
    }

    @Override
    public int getSize() {
        return categorias.size();
    }

    @Override
    public Categoria getElementAt(int index) {
        return categorias.get(index);
    }

    public Categoria getCategoriaAt(int index) {
        if (index < 0 || index >= categorias.size()) {
            return null;
        }
        return categorias.get(index);
    }
}
