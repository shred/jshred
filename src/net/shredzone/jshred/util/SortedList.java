/*
 * jshred -- Shred's Toolbox
 *
 * Copyright (c) 2004 Richard "Shred" Körber
 *   http://www.shredzone.net/go/jshred
 *
 *-----------------------------------------------------------------------
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is JSHRED.
 *
 * The Initial Developer of the Original Code is
 * Richard "Shred" Körber.
 * Portions created by the Initial Developer are Copyright (C) 2004
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK *****
 */

package net.shredzone.jshred.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

/**
 * A SortedList is a List which sorts its elements according to a certain
 * order. The natural sorting order of the elements is used unless a
 * Comparator was given in the constructor.
 * <p>
 * The SortedList does not allow null elements or duplicate elements. An
 * element is considered equal if <code>equal()</code> returns true.
 * <p>
 * Note that a SortedList is not synchronized! Use <code>Collections.synchronizedList()</code>
 * if you need synchronized access.
 * <p>
 * (Generics are not yet supported, sorry...)
 *
 * @author  Richard Körber &lt;dev@shredzone.de&gt;
 * @since   R7
 * @version $Id: SortedList.java,v 1.4 2005/01/11 19:37:41 shred Exp $
 */
public class SortedList extends ArrayList {
  private static final long serialVersionUID = 3257003263499972657L;
  private Comparator comparator = null;     // Comparator to be used
  
  /**
   * Create an empty SortedList with natural order.
   */
  public SortedList() {
  }
  
  /**
   * Create a new SortedList and initialize it with the given Collection.
   * The collection entries are inserted as they are found in the Collection,
   * and will be properly sorted in this list even if the Collection was
   * unsorted.
   * 
   * @param   col     Collection to initialize the SortedList with.
   */
  public SortedList( Collection col ) {
    addAll( col );
  }
  
  /**
   * Create a SortedList that uses the given Comparator instead of the
   * element's natural order. Note that once set, a Comparator cannot be
   * changed or removed.
   * 
   * @param   c         Comparator to be used
   */
  public SortedList( Comparator c ) {
    comparator = c;
  }

  /**
   * Get the Comparator used for comparison, or null if natural order is
   * to be used.
   * 
   * @return    Comparator or null
   */
  public Comparator comparator() {
    return comparator;
  }
  
  /**
   * Add an element to a certain index of this list. This method is not
   * permitted in a SortedList and thus throws an exception.
   *
   * @param   index       Index to add an element to
   * @param   element     Element to be added
   */
  public void add( int index, Object element ) {
    throw new UnsupportedOperationException("not supported");
  }

  /**
   * Add an element to the SortedList. The element will be sorted to its
   * proper position. If an equal element has already been added to this
   * SortedList, it will be replaced by this element. A null element is not
   * allowed in a SortedList, and will throw a NullPointerException.
   * 
   * @param   o       Object to be added.
   * @return  Always true.
   * @throws  ClassCastException
   * @see java.util.Collection#add(java.lang.Object)
   */
  public boolean add( Object o ) {
    //--- Assert an element ---
    if( o==null )
      throw new NullPointerException();
    
    //--- Find its position in the list ---
    int ix = approximate( o );

    if( ix==size() ) {
      //--- Add a new maximum element ---
      super.add( o );

    }else if( compare( get(ix), o )==0  ) {
      //--- Replace an existing element ---
      super.set( ix, o );
      
    }else {
      //--- Insert an element ---
      super.add( ix, o );
    }
    
    return true;
  }

  /**
   * Add all elements of a Collection to the SortedList, starting at a
   * certain index. This method is not permitted in a SortedList and thus
   * throws an exception.
   * 
   * @param   index       Starting index
   * @param   c           Collection to be added
   * @return  true if this list was changed
   */
  public boolean addAll( int index, Collection c ) {
    throw new UnsupportedOperationException("not supported");
  }

  /**
   * Add all elements of a Collection to the SortedList. The entries are
   * inserted as they are found in the Collection, and sorted to their
   * proper position.
   * 
   * @param   c           Collection to be added.
   * @return  true if this list was changed
   * @see java.util.Collection#addAll(java.util.Collection)
   */
  public boolean addAll( Collection c ) {
    Iterator it = c.iterator();
    while( it.hasNext() ) {
      add( it.next() );      
    }
    return true;
  }

  /**
   * The set method is not supported by a SortedList.
   * 
   * @param   index       Index to set an element.
   * @param   element     The element to be set.
   * @return  The previous element at the index.
   */
  public Object set( int index, Object element ) {
    throw new UnsupportedOperationException("not supported");
  }

  /**
   * Check if a SortedList contains an element. The list contains an element
   * if the given element is <code>equal()</code> to one of the elements in
   * this list.
   * 
   * @param   elem        Element to be checked
   * @return  true if the list contains the element
   */
  public boolean contains( Object elem ) {
    if( elem==null )
      throw new NullPointerException();
    
    int ix = approximate( elem );
    return( ix<size() && compare( get(ix), elem )==0  );
  }
  
  /**
   * Get the index of an element.
   * 
   * @param   elem        Element to be found
   * @return  index of that element, or -1 if it was not found
   */
  public int indexOf( Object elem ) {
    if( elem==null )
      throw new NullPointerException();
    
    int ix = approximate( elem );
    if( ix<size() && compare( get(ix), elem )==0  )
      return ix;
    else
      return -1;
  }
  
  /**
   * Get the index of the last occurance of an element. Since there are no
   * duplicate entries in a SortedList, the result is the same as from
   * <code>indexOf()</code>.
   * 
   * @param   elem        Element to be found
   * @return  index of that element, or -1 if it was not found
   */
  public int lastIndexOf( Object elem ) {
    return indexOf( elem );
  }
  
  /**
   * Approximate the position of the given Object in the list. The element
   * at the returned index is either equal to the given object, or it is
   * the next higher element. This would be the insert index for the object
   * passed in.
   * <p>
   * NOTE: The returned index will be size() if the given object is higher
   * than all current objects in the list.
   * 
   * @param   o         Object to approximate
   * @return  Index of the approximation.
   * @throws  ClassCastException
   */
  private int approximate( Object o ) {
    //--- Empty list will always return 0 ---
    if( isEmpty() ) return 0;
    
    //--- Check if o is higher than the last object ---
    int size = size();
    if( compare( get( size-1 ), o ) < 0 )
      return size;
    
    //--- Find the position ---
    int min = 0;
    int max = size-1;
    int current = 0;
    int oldcurrent = -1;
    
    while( oldcurrent!=current ) {
      oldcurrent = current;
      current = (min+max)/2;
      Object cmp = get( current );

      int result = compare( cmp, o );
      if( result<0 ) {
        // cmp is less than o
        min = current;

      }else if( result>0 ) {
        // cmp is greater than o
        max = current;
        
      }else {
        // o equals cmp
        break;
      }
    };
    
    if( compare( get(current), o )<0 )
      current += 1;
    
    return current;
  }
  
  /**
   * Compares two objects. If a comparator was set, it is used for comparison.
   * If none was set, the natural order of the objects will be compared.
   * 
   * @param   cmp       Object 1
   * @param   cmp2      Object 2
   * @return  negative if cmp is less than cmp2,
   *          positive is cmp is greater than cmp2,
   *          zero if cmp is equal to cmp2.
   * @throws  ClassCastException if the two objects were not comparable.
   */
  private int compare( Object cmp, Object cmp2 ) {
    if( comparator!=null ) {
      return comparator.compare( cmp, cmp2 );
    }else {
      Comparable co = (Comparable) cmp;     // provoke a ClassCastException
      return co.compareTo( cmp2 );          // if cmp is not a Comparable
    }
  }
    
}
