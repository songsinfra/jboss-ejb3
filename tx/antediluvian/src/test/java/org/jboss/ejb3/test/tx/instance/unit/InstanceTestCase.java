/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
  *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.ejb3.test.tx.instance.unit;

import static org.junit.Assert.fail;

import javax.ejb.EJBException;
import javax.ejb.EJBTransactionRequiredException;
import javax.naming.InitialContext;
import javax.transaction.TransactionManager;

import org.jboss.ejb3.test.tx.common.AbstractTxTestCase;
import org.jboss.ejb3.test.tx.common.SimpleContainer;
import org.jboss.ejb3.test.tx.instance.InstanceTest;
import org.junit.Test;

/**
 * @author <a href="mailto:carlo.dewolf@jboss.com">Carlo de Wolf</a>
 * @version $Revision: $
 */
public class InstanceTestCase extends AbstractTxTestCase
{
   /**
    * Test whether the stateful bean is still alive after a transaction
    * guard failure.
    */
   @Test
   public void testInstance() throws Throwable
   {
      SimpleContainer<?> container = bootstrap.lookup("TestContainer", SimpleContainer.class);
      
      InstanceTest bean = container.constructProxy(InstanceTest.class);
      
      InitialContext ctx = new InitialContext();
      TransactionManager tm = (TransactionManager) ctx.lookup("java:/TransactionManager");
      tm.begin();
      try
      {
         bean.never();
         fail("never should not allow a transaction");
      }
      catch(EJBException e)
      {
         // Good
      }
      finally
      {
         tm.rollback();
      }
      
      bean.required();
   }
   
   @Test
   public void testTransactionMandatory() throws Throwable
   {
      SimpleContainer<?> container = bootstrap.lookup("TestContainer", SimpleContainer.class);
      
      InstanceTest bean = container.constructProxy(InstanceTest.class);
      
      InitialContext ctx = new InitialContext();
      TransactionManager tm = (TransactionManager) ctx.lookup("java:/TransactionManager");
      tm.begin();
      try
      {
         bean.mandatory();
      }
      finally
      {
         tm.rollback();
      }
   }
   
   @Test
   public void testTransactionMandatoryIllegal() throws Throwable
   {
      SimpleContainer<?> container = bootstrap.lookup("TestContainer", SimpleContainer.class);
      
      InstanceTest bean = container.constructProxy(InstanceTest.class);
      try
      {
         bean.mandatory();
         fail("transaction is required");
      }
      catch(EJBTransactionRequiredException e)
      {
         // Good
      }
   }
}
