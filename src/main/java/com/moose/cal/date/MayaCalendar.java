/*****************************************************************************
Copyright 2015 Hypotemoose, LLC.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*****************************************************************************/
package com.moose.cal.date;

import com.moose.cal.util.*;
import com.moose.cal.astro.*;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * A date in the Maya calendar.
 * <p>
 * The Maya calendar is not one calendar per se, but rather a system of
 * calendars employed by pre-Columbian Mesoamerica. In many modern communities
 * in Guatemala and Mexico, this calendar is still used today.
 * <p>
 * The Maya calendar consists of several cycles (or counts) of different
 * lengths. The 260-day calendar, or <em>Tzolk'in</em>, was combined with
 * the 365-day calendar, or <em>Haab'</em> to form a synchronized cycle
 * lasting for 52 Haab' called the Calendar Round. To measure periods longer
 * than this, the Maya used the Long Count calendar.
 * <p>
 * The Long Count calendar is a vigesimal (base-20) and base-18 calendar.
 * Conversion between Maya count units are as follows:
 * <code>
 * Long Count Unit  |  Long Count Period  | Days
 * 1 K'in           |                     | 1
 * 1 Uinal          | 20 K'in             | 20
 * 1 Tun            | 18 Uinal            | 360
 * 1 K'atun         | 20 Tun              | 7,200
 * 1 B'ak'tun       | 20 K'atun           | 144,000
 * 1 Piktun         | 20 B'ak'tun         | 2,880,000
 * 1 Kalabtun       | 20 Piktun           | 57,600,000
 * 1 K'inchiltun    | 20 Kalabtun         | 1,152,000,000
 * 1 Alautun        | 20 K'inchiltun      | 23,040,000,000
 * </code>
 * <p>
 * Note: It is a common misconception to use the term "Mayan" calendar, when
 * this term is actually an Indo-European invention. It is unlikely that this
 * would cause anyone to be upset, and in our contemporary vernacular these
 * words have become interchangeable.
 * @author Chris Engelsma
 * @version 1.1
 * @since 2015.08.24
 */ 
public class MayaCalendar implements Almanac {
  public static final String CALENDAR_NAME = "Maya Calendar";
  public static final JulianDay EPOCH = new JulianDay(584282.5);
  
  /**
   * Constructs a date in the Maya calendar.
   * The calendar will default to today's date.
   */
  public MayaCalendar() {
    this(new JulianDay());
  }
  
  /**
   * Constructs a Maya date.
   * @param jday a Julian day.
   */
  public MayaCalendar(JulianDay jday) {
    _julianDayToMayaCount(jday);
  }

  /**
   * Constructs a Maya date.
   * @param baktun a specified B'aktun.
   * @param katun  a specified K'atun.
   * @param tun    a specified Tun.
   * @param uinal  a specified Uinal.
   * @param kin    a specified K'in.
   */
  public MayaCalendar(int baktun, int katun, int tun, int uinal, int kin) {
    _baktun = baktun;
    _katun = katun;
    _tun = tun;
    _uinal = uinal;
    _kin = kin;
  }
  
  /**
   * Gets this K'in.
   * The K'in is the smallest unit of Maya calendar time. It is equal to 1
   * day.
   * @return This K'in.
   */
  public int getKin() { 
    return _kin;    
  }

  /**
   * Gets this Uinal.
   * A Uinal is equal to 20 K'in, which is equal to 20 days.
   * @return This Uinal.
   */
  public int getUinal() { 
    return _uinal;  
  }

  /**
   * Gets this Tun.
   * A Tun is equal to 18 Uinal, which is equal to 360 days.
   * @return This Tun.
   */ 
  public int getTun() { 
    return _tun;
  }

  /**
   * Gets this K'atun.
   * A K'atun is equal to 20 Tun, which is equal to 7,200 days.
   * @return This K'atun.
   */
  public int getKatun() { 
    return _katun;
  }

  /**
   * Gets this B'aktun.
   * A B'aktun is equal to 20 K'atun which is equal to 144,000 days.
   * @return This B'aktun.
   */
  public int getBaktun() {
    return _baktun;
  }
  
  /**
   * Prints the date.
   * @param the date.
   */
  @Override
  public String getDate() {
    return new String( _baktun+"."+
                       _katun+"."+
                       _tun+"."+
                       _uinal+"."+
                       _kin);
  }

  @Override
  public String toString() {
    return(CALENDAR_NAME+": "+getDate());
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof MayaCalendar))
      return false;
    if (obj == this)
      return true;
      
    final MayaCalendar date = (MayaCalendar) obj;
    return new EqualsBuilder()
      .append(_kin, date.getKin())
      .append(_uinal, date.getUinal())
      .append(_tun, date.getTun())
      .append(_katun, date.getKatun())
      .append(_baktun, date.getBaktun())
      .isEquals();
  }
  
  @Override
  public int hashCode() {
    return new HashCodeBuilder()
      .append(_kin)
      .append(_uinal)
      .append(_tun)
      .append(_katun)
      .append(_baktun)
      .toHashCode();
  }

/////////////////////////////////////////////////////////////////////////////
// private

  private void _julianDayToMayaCount(JulianDay jday) {
    double day = (jday.atMidnight()).getValue();
    double d = day - EPOCH.getValue();
    _baktun = (int)Math.floor(d/_lbaktun);
    d = d % _lbaktun;
    _katun = (int)Math.floor(d/_lkatun);
    d = d % _lkatun;
    _tun = (int)Math.floor(d / _ltun);
    d = d % _ltun;
    _uinal = (int)Math.floor(d / _luinal);
    _kin = (int)(d % _luinal);
  }

  private int _kin;
  private int _uinal;
  private int _tun;
  private int _katun;
  private int _baktun;
  private int _piktun;
  private int _kalabtun;
  private int _kinchiltun;
  private int _alautun;


  /* Constants for Maya Calendar */
  private double _luinal      = 20.0;
  private double _ltun        = 360.0;
  private double _lkatun      = 7200.0;
  private double _lbaktun     = 144000.0;
  private double _lpiktun     = 2880000.0;
  private double _lkalabtun   = 57600000.0;
  private double _lkinchiltun = 1152000000.0;
  private double _lalautun    = 23040000000.0;

  private String[] _haabMonths = {
    "Pop",
    "Wo'",
    "Sip",
    "Sotz'",
    "Sek",
    "Xul",
    "Yaxk'in'",
    "Mol",
    "Ch'en",
    "Yax",
    "Sak'",
    "Keh",
    "Mak",
    "K'ank'in",
    "Muwan'",
    "Pax",
    "K'ayab",
    "Kumk'u",
    "Wayeb"
  };

  private String[] _tzolkinDayNames = {
    "Imix'",
    "Ik'",
    "Ak'b'al",
    "K'an",
    "Chikchan",
    "Kimi",
    "Manik'",
    "Lamat",
    "Muluk",
    "Ok",
    "Chuwen",
    "Eb'",
    "B'en",
    "Ix",
    "Men",
    "Kib'",
    "Kab'an",
    "Etz'nab'",
    "Kawak",
    "Ajaw"
  };
}
