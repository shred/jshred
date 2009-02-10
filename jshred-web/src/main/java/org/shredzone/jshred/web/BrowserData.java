/*
 * jshred -- Shred's Toolbox
 *
 * Copyright (c) 2009 Richard "Shred" Körber
 *   http://jshred.shredzone.org
 *-----------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.shredzone.jshred.web;

import java.util.Iterator;

/**
 * Contains all data required to show a page browser.
 * 
 * @author Richard "Shred" Körber
 * @version $Revision: 257 $
 */
public class BrowserData {
    private int pagecount;
    private int page;
    private long resultcount;
    private String baseurl;
    private String pageparam = "page";

    /**
     * Gets the number of pages to be shown.
     * 
     * @return Number of pages
     */
    public int getPagecount() { return pagecount; }
    public void setPagecount(int pagecount) {
        this.pagecount = pagecount;
        if (page >= pagecount) page = pagecount-1;
    }

    /**
     * Gets a good guess for large step increments. This implementation depends
     * on the current pagecount and always returns a decimal power in the
     * magnitude of the pagecount.
     * <p>
     * Examples: pagecount=14, pagesteps=10. pagecount=321, pagesteps=100.
     * pagecount=812312, pagesteps=100000.
     * <p>
     * For a pagecount of 0, 0 is be returned.
     * 
     * @return Large step increments.
     */
    public int getPagesteps() {
        if (getPagecount() == 0) return 0;
        return (int) Math.pow(10, Math.floor(Math.log10(getPagecount())));
    }

    /**
     * Gets the page currently displayed.
     * 
     * @return Page number currently displayed.
     */
    public int getPage() { return page; }
    public void setPage(int page) {
        if (page >= pagecount) {
            page = pagecount - 1;
        }
        if (page < 0) {
            page = 0;
        }
        this.page = page;
    }

    /**
     * Gets the number of entries of the result. This is only used as
     * information to the user, and has no direct impact to the browser.
     *  
     * @return Number of entries
     */
    public long getResultcount()                 { return resultcount; }
    public void setResultcount(long resultcount) { this.resultcount = resultcount; }

    /**
     * Returns the URL to be loaded when another page is to be shown. This is
     * usually the URL of the page currently shown.
     * 
     * @return Base URL
     */
    public String getBaseurl()                  { return baseurl; }
    public void setBaseurl(String baseurl)      { this.baseurl = baseurl; }
    
    /**
     * Returns the parameter name containing the page currently shown in
     * this browser. Usually "page".
     * 
     * @return Page parameter name
     */
    public String getPageparam()                { return pageparam; }
    public void setPageparam(String pageparam)  { this.pageparam = pageparam; }
    
    /**
     * Returns an iterator that iterates thru a sequence of page numbers.
     * The sequence can be used in the browser fragment to select other
     * page numbers.
     * <p>
     * The current state of the BrowserData is frozen in this iterator.
     * <p>
     * Note that the Iterator starts counting from 1 to make the result
     * readable for humans, while the browser itself counts the page starting
     * from zero.
     * 
     * @return  Iterator that iterates through a sequence of page numbers
     */
    public Iterator<Integer> getPageIterator() {
        return new PageNumberIterator(
                getPage(),
                getPagesteps(),
                getPagecount()
        );
    }

    /**
     * This class provides the page number iterator.
     */
    private static class PageNumberIterator implements Iterator<Integer> {
        /*TODO: Make this configurable some day... */
        private final int BORDER_PAGES = 3;
        private final int CENTER_PAGES = 4;

        private final int pageDisplayed;
        private final int steps;
        private final int pageCount;
        private int currentPage = 1;

        /**
         * Create a new page number iterator.
         * 
         * @param page      Page that is currently shown, starting from 0.
         * @param steps     Large step rate between both ends of the scale.
         *      Usually pass the result of {@link BrowserData#getPagesteps()}
         *      here.
         * @param count     Total number of pages.
         */
        public PageNumberIterator(int page, int steps, int count) {
            this.pageDisplayed = page;
            this.steps = steps;
            this.pageCount = count;
        }

        /**
         * Returns true if there is another page number to be shown.
         * 
         * @return <code>true</code>: has more page numbers
         */
        public boolean hasNext() {
            return currentPage <= pageCount;
        }

        /**
         * Returns the next page number in the sequence.
         * 
         * @return Next page number
         */
        public Integer next() {
            int result = currentPage;
            
            int nextPage = pageCount + 1;
            
            // The next page number is the nearest proposal that is still
            // within the scale, and is at least one page ahead.
            int nextStep = proposeNextStep(currentPage);
            if (nextStep > currentPage && nextStep < nextPage) nextPage = nextStep;
            
            int nextStart = proposeNextStart(currentPage);
            if (nextStart > currentPage && nextStart < nextPage) nextPage = nextStart;
            
            int nextEnd = proposeNextEnd(currentPage);
            if (nextEnd > currentPage && nextEnd < nextPage) nextPage = nextEnd;

            int nextCenter = proposeNextCenter(currentPage);
            if (nextCenter > currentPage && nextCenter < nextPage) nextPage = nextCenter;

            currentPage = nextPage;
            return result;
        }

        /**
         * Unsupported operation!
         */
        public void remove() {
            throw new UnsupportedOperationException();
        }
        
        /**
         * Propose the next general page. This is the next page of the given
         * step rate. (E.g. if the current page is 184 and the step rate is 100,
         * the next proposed page is 200). 
         * 
         * @param current   Current page
         * @return  Next proposed page
         */
        private int proposeNextStep(int current) {
            return ((current / steps) + 1) * steps;
        }

        /**
         * Proposes the next page on the left side of the scale. This are
         * the first {@link #BORDER_PAGES} number of pages.
         * 
         * @param current   Current page
         * @return  Next proposed page
         */
        private int proposeNextStart(int current) {
            if (current < BORDER_PAGES) {
                return current + 1;
            } else {
                return pageCount + 1;
            }
        }
        
        /**
         * Proposes the next page on the right side of the scale. This are
         * the last {@link #BORDER_PAGES} number of pages.
         * 
         * @param current   Current page
         * @return  Next proposed page
         */
        private int proposeNextEnd(int current) {
            if (current > pageCount - BORDER_PAGES) {
                return current + 1;
            } else {
                return pageCount - BORDER_PAGES + 1;
            }
        }
        
        /**
         * Proposes a number of pages around the current page number, with
         * the current page number being in the center. Usually there are
         * {@link #CENTER_PAGES} pages each to the left and right of the
         * current page number.
         * 
         * @param current   Current page
         * @return  Next proposed page
         */
        private int proposeNextCenter(int current) {
            if (Math.abs(current - pageDisplayed) <= CENTER_PAGES) {
                return current + 1;
            } else {
                return pageDisplayed - CENTER_PAGES + 1;
            }
        }
    }
    
}
