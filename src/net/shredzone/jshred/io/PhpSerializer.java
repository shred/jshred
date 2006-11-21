/*
 * jshred -- Shred's Toolbox
 *
 * Copyright (c) 2006 Richard "Shred" Körber
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

package net.shredzone.jshred.io;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * PhpSerializer is a Writer that is able to write out a Java object so
 * PHP's <code>unserialize()</code> function is able to read the result
 * into a PHP variable.
 * <p>
 * The converter tries to find appropriate data types for conversion. The
 * basic data types are as well supported as arrays, collections and maps.
 * <p>
 * Do not serialize more than one object, since more is not supported by
 * PHP. If you need to pass several objects, pack them into an array and
 * serialize that array.
 * <p>
 * Note that Java Objects are not serialized in a manner that could be
 * fully restored on PHP side. Try to avoid sending pure Java Objects since
 * the result is unpredictable (in best case, it's just the
 * <code>toString()</code> result that is being serialized).
 * <p>
 * Recursive references to the same object (i.e. an array with an element
 * refering to that array) are not supported yet and will lead to a stack
 * overflow.
 *
 * @author  Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id:$
 * @since   R14
 */
public class PhpSerializer extends FilterWriter {
  
  /**
   * Create a new PhpSerializer.
   * 
   * @param w   Writer to write the output to.
   */
  public PhpSerializer( Writer w ) {
    super(w);
  }

  /**
   * Write an integer.
   * 
   * @param i   Integer value to write
   */
  public void serialize( int i ) throws IOException {
    write( "i:" );
    write( String.valueOf( i ) );
    write( ";" );
  }
  
  /**
   * Write a long.
   * 
   * @param i   Long value to write
   */
  public void serialize( long i ) throws IOException {
    write( "i:" );
    write( String.valueOf( i ) );
    write( ";" );
  }

  /**
   * Write a boolean.
   * 
   * @param b   Boolean value to write
   */
  public void serialize( boolean b ) throws IOException {
    write( "b:" );
    write( b ? "1" : "0" );
    write( ";" );
  }
  
  /**
   * Write a double.
   * 
   * @param d   Double value to write
   */
  public void serialize( double d ) throws IOException  {
    write( "d:" );
    write( String.valueOf( d ) );
    write( ";" );
  }
  
  /**
   * Write a float.
   * 
   * @param f   Float value to write
   */
  public void serialize( float f ) throws IOException {
    serialize( (double) f );
  }
  
  /**
   * Write a char
   * 
   * @param c   Char value to write
   */
  public void serialize( char c ) throws IOException {
    serialize( String.valueOf( c ) );
  }
  
  /**
   * Write a char array
   * 
   * @param c   Array of chars to write
   */
  public void serialize( char[] c ) throws IOException {
    serialize( String.valueOf( c ) );
  }

  /**
   * Write a section of a char array
   * 
   * @param c   Array of chars to write
   * @param offset  First index to write
   * @param count   Number of chars to write
   */
  public void serialize( char[] c, int offset, int count ) throws IOException {
    serialize( String.valueOf( c, offset, count ) );
  }

  /**
   * Serialize a String
   * 
   * @param s   String to write, may be null
   */
  public void serialize( String s ) throws IOException {
    if( s==null ) {
      serializeNull();
    }else {
      write( "s:" );
      write( String.valueOf( s.length() ) );
      write( ":\"" );
      write( s );
      write( "\";" );
    }
  }

  /**
   * Serialize a Number. BigDecimal and BigInteger is supported, at least
   * on the Java side. It is not known how PHP behaves when passing very large
   * numbers.
   * 
   * @param n   Number to write, may be null
   */
  public void serialize( Number n ) throws IOException {
    if( n==null ) {
      serializeNull();
    }else if( n instanceof BigDecimal ) {
      write( "d:" );
      write( ((BigDecimal) n).toString() );
      write( ";" );
    }else if( n instanceof BigInteger ) {
      write( "i:" );
      write( ((BigInteger) n).toString() );
      write( ";" );
    }else if( n instanceof Float || n instanceof Double ) {
      serialize( n.doubleValue() );
    }else {
      serialize( n.longValue() );
    }
  }

  /**
   * Serialize an array. Each element of that array is serialized.
   * <p>
   * Note that the array itself must not be an element of that array.
   * 
   * @param a   Array to write, may be null
   */
  public void serialize( Object[] a ) throws IOException {
    if( a==null ) {
      serializeNull();
    }else {
      serialize( Arrays.asList( a ) );
    }
  }
  
  /**
   * Serialize a Collection. The collection's iterator is used to serialize
   * each entry of the Collection. On PHP side, an index array is created,
   * with the index counting from 0.
   * <p>
   * The collection may contain <code>null</code> values and also nested
   * arrays, collections or maps.
   * <p>
   * Note that the collection must not contain a reference to itself.
   * 
   * @param c   Collection to write, may be null
   */
  public void serialize( Collection c ) throws IOException {
    if( c==null ) {
      serializeNull();
    }else {
      write( "a:" );
      write( String.valueOf( c.size() ) );
      write( ":{" );
      long cnt=0;
      Iterator it = c.iterator();
      while( it.hasNext() ) {
        Object o = it.next();
        serialize( cnt++ );
        serialize( o );
      }
      write( "}" );
    }
  }
  
  /**
   * Serialize a map. The map's key iterator is used to serialize each
   * entry of the Map. On PHP side, an associative array is created, with
   * the key being the map's key, and the value being the map's appropriate
   * value.
   * <p>
   * The map may contain not more than one <code>null</code> key. Due to
   * PHP limitations, the key is always resolved using <code>toString()</code>
   * unless it is a Number object. This means that it is not possible to
   * use collections, maps or arrays as map keys.
   * <p>
   * The map's values may contain <code>null</code> and also nested
   * arrays, collections or maps.
   * <p>
   * Note that the map must not contain a reference to itself.
   * 
   * @param m   Map to write, may be null
   */
  public void serialize( Map m ) throws IOException {
    if( m==null ) {
      serializeNull();
    }else {
      write( "a:" );
      write( String.valueOf( m.size() ) );
      write( ":{" );
      Iterator it = m.keySet().iterator();
      while( it.hasNext() ) {
        Object key = it.next();
        Object value = m.get( key );
        
        // Key needs a special treatment, since it must be serialized
        // either as integer or as string.
        if( key==null ) {
          write("s:0:\"\";");
        }else if( key instanceof Boolean ) {
          write("i:");
          write( ((Boolean) key).booleanValue() ? "1" : "0" );
          write(";");
        }else if( key instanceof Number ) {
          if( key instanceof Float || key instanceof Double || key instanceof BigDecimal ) {
            serialize( key.toString() );
          }else {
            write("i:");
            write( key.toString() );
            write(";");
          }
        }else {
          serialize( key.toString() );
        }
  
        // Just serialize the value though
        serialize( value );
      }
      write( "}" );
    }
  }
  
  /**
   * Serialize an Object. If the object is found to be an instance of
   * String, Character, Boolean, Number, Collection or Map, or if the object
   * is an array, the appropriate <code>serialize()</code> method is used.
   * In all other cases, the object's <code>toString()</code> result
   * is serialized as a string.
   * <p>
   * Note that in future versions, Serializable objects may be serialized
   * into PHP objects. For now, try to avoid to pass Serializable objects
   * unless they are one of the basic types.
   * 
   * @param o   Object to write, may be null
   */
  public void serialize( Object o ) throws IOException {
    if( o==null ) {
      serializeNull();
    }else if( o instanceof Number ) {
      serialize( (Number) o );
    }else if( o instanceof Collection ) {
      serialize( (Collection) o );
    }else if( o instanceof Map ) {
      serialize( (Map) o );
    }else if( o instanceof Object[] ) {
      serialize( (Object[]) o );
    }else if( o instanceof Boolean ) {
      serialize( ((Boolean) o).booleanValue() );
    }else if( o instanceof Character ) {
      serialize( ((Character) o).charValue() );
    }else {
      serialize( o.toString() );
    }
  }

  /**
   * Write a mere null pointer.
   */
  public void serializeNull() throws IOException {
    write("N;");
  }

}
