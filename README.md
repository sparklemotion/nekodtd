# NekoDTD

This repo is a mirror of the original NekoDTD. Although I couldn't find the original repo
I created this repo from the latest tar file that is in the download section.

For the original project page, see http://people.apache.org/~andyc/neko/doc/dtd/index.html


# Release process

## Building and packaging

Run `./build.rb` and make sure to sign with a relevant GPG key.

## Upload to Maven Central

Follow instructions at https://central.sonatype.org/publish/publish-manual/#bundle-creation

- log in to https://s01.oss.sonatype.org/
- click "Staging Upload"
- select "Artifact Bundle"
- Upload the `bundle.jar` created by `build.rb`

## Release

Follow instructions at https://central.sonatype.org/publish/release/#locate-and-examine-your-staging-repository

- find the Staging Repository
- select it
- If everything looks good, hit the "Release" button


# Copyright

The CyberNeko Software License, Version 1.0

(C) Copyright 2002,2003, Andy Clark.  All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in
   the documentation and/or other materials provided with the
   distribution.

3. The end-user documentation included with the redistribution,
   if any, must include the following acknowledgment:
     "This product includes software developed by Andy Clark."
   Alternately, this acknowledgment may appear in the software itself,
   if and wherever such third-party acknowledgments normally appear.

4. The names "CyberNeko" and "NekoHTML" must not be used to endorse
   or promote products derived from this software without prior
   written permission. For written permission, please contact
   andy@cyberneko.net.

5. Products derived from this software may not be called "NekoHTML",
   nor may "NekoHTML" appear in their name, without prior written
   permission of the author.

THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR OTHER CONTRIBUTORS
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

====================================================================

This license is based on the Apache Software License, version 1.1.
