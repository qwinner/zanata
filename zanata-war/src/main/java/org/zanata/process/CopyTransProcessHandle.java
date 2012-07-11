/*
 * Copyright 2010, Red Hat, Inc. and individual contributors as indicated by the
 * @author tags. See the copyright.txt file in the distribution for a full
 * listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.zanata.process;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.zanata.common.CopyTransOptions;
import org.zanata.model.HProjectIteration;

/**
 * Process Handle for a background copy trans.
 *
 * @author Carlos Munoz <a href="mailto:camunoz@redhat.com">camunoz@redhat.com</a>
 */
@RequiredArgsConstructor
public class CopyTransProcessHandle extends ProcessHandle
{

   @Getter
   private final HProjectIteration projectIteration;

   @Getter
   private final String triggeredBy;

   @Getter
   private final CopyTransOptions options;

   @Getter
   @Setter
   private int documentsProcessed;

   @Getter
   @Setter
   private String cancelledBy;

   @Getter
   @Setter
   private long cancelledTime;
}